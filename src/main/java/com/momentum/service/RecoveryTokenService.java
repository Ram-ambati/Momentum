package com.momentum.service;

import com.momentum.entity.*;
import com.momentum.exception.ApiException;
import com.momentum.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class RecoveryTokenService {
    private final AppUserRepository userRepository;
    private final TaskTemplateRepository taskTemplateRepository;
    private final TaskInstanceRepository taskInstanceRepository;
    private final RecoveryTokenUsageRepository recoveryTokenUsageRepository;
    private final RecoveryTokenTransactionRepository recoveryTokenTransactionRepository;

    public RecoveryTokenService(AppUserRepository userRepository,
                                TaskTemplateRepository taskTemplateRepository,
                                TaskInstanceRepository taskInstanceRepository,
                                RecoveryTokenUsageRepository recoveryTokenUsageRepository,
                                RecoveryTokenTransactionRepository recoveryTokenTransactionRepository) {
        this.userRepository = userRepository;
        this.taskTemplateRepository = taskTemplateRepository;
        this.taskInstanceRepository = taskInstanceRepository;
        this.recoveryTokenUsageRepository = recoveryTokenUsageRepository;
        this.recoveryTokenTransactionRepository = recoveryTokenTransactionRepository;
    }

    public RecoveryTokenView get(AppUser user) {
        List<RecoveryTokenTransaction> txns = recoveryTokenTransactionRepository.findByUserOrderByIdDesc(user);
        return new RecoveryTokenView(user.getRecoveryTokenBalance(), txns.stream().map(RecoveryTxnRow::from).toList());
    }

    @Transactional
    public RecoveryTokenView grant(AppUser user, int amount, String reason) {
        if (amount <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Amount must be > 0");
        }
        AppUser locked = userRepository.findByIdForUpdate(user.getId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        locked.addRecoveryTokens(amount);
        userRepository.save(locked);

        RecoveryTokenTransaction txn = new RecoveryTokenTransaction();
        txn.setUser(locked);
        txn.setAmount(amount);
        txn.setTransactionType(RecoveryTokenTransactionType.EARN);
        txn.setReason(reason == null || reason.isBlank() ? "Manual grant" : reason);
        recoveryTokenTransactionRepository.save(txn);
        return get(locked);
    }

    @Transactional
    public RecoveryTokenView use(AppUser user, UseRecoveryTokenRequest request) {
        AppUser locked = userRepository.findByIdForUpdate(user.getId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (locked.getRecoveryTokenBalance() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No recovery tokens available");
        }

        LocalDate today = LocalDate.now(ZoneId.of(locked.getTimezone()));
        LocalDate date = request.date();
        if (date.isAfter(today)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Recovery date cannot be in the future");
        }

        RecoveryScope scope;
        TaskTemplate template = null;
        if (request.templateId() == null) {
            scope = RecoveryScope.OVERALL;
            if (recoveryTokenUsageRepository.existsByUserAndScopeAndRecoveryDate(locked, scope, date)) {
                throw new ApiException(HttpStatus.CONFLICT, "Recovery token already used for this day");
            }
            List<TaskInstance> day = taskInstanceRepository.findByUserAndTaskDateOrderById(locked, date);
            if (day.isEmpty()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "No tasks exist for requested day");
            }
            boolean dayAlreadySuccessful = day.stream().allMatch(t -> t.getStatus() == TaskStatus.COMPLETED || t.getStatus() == TaskStatus.CANCELLED);
            if (dayAlreadySuccessful) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Recovery token can only be used on a broken day");
            }
        } else {
            scope = RecoveryScope.TEMPLATE;
            template = taskTemplateRepository.findByIdAndUser(request.templateId(), locked)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Task template not found"));

            if (recoveryTokenUsageRepository.existsByUserAndTemplateAndRecoveryDate(locked, template, date)) {
                throw new ApiException(HttpStatus.CONFLICT, "Recovery token already used for this task/day");
            }

            TaskTemplate finalTemplate = template;
            TaskInstance task = taskInstanceRepository.findByUserAndTemplateAndTaskDate(locked, finalTemplate, date)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "No task instance exists for template/date"));
            if (task.getStatus() == TaskStatus.COMPLETED || task.getStatus() == TaskStatus.CANCELLED) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Task is already completed/cancelled");
            }
        }

        locked.addRecoveryTokens(-1);
        userRepository.save(locked);

        RecoveryTokenUsage usage = new RecoveryTokenUsage();
        usage.setUser(locked);
        usage.setScope(scope);
        usage.setTemplate(template);
        usage.setRecoveryDate(date);
        recoveryTokenUsageRepository.save(usage);

        RecoveryTokenTransaction txn = new RecoveryTokenTransaction();
        txn.setUser(locked);
        txn.setAmount(-1);
        txn.setTransactionType(RecoveryTokenTransactionType.SPEND);
        txn.setReason(scope == RecoveryScope.OVERALL ? "Used overall streak recovery for " + date : "Used task streak recovery for " + template.getTitle() + " on " + date);
        recoveryTokenTransactionRepository.save(txn);

        return get(locked);
    }

    public record UseRecoveryTokenRequest(LocalDate date, Long templateId) {}
    public record RecoveryTokenView(long balance, List<RecoveryTxnRow> transactions) {}
    public record RecoveryTxnRow(Long id, int amount, String type, String reason) {
        static RecoveryTxnRow from(RecoveryTokenTransaction t) {
            return new RecoveryTxnRow(t.getId(), t.getAmount(), t.getTransactionType().name(), t.getReason());
        }
    }
}
