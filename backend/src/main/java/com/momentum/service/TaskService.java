package com.momentum.service;

import com.momentum.entity.*;
import com.momentum.exception.ApiException;
import com.momentum.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class TaskService {
    private final TaskTemplateRepository taskTemplateRepository;
    private final TaskInstanceRepository taskInstanceRepository;
    private final XpTransactionRepository xpTransactionRepository;
    private final CoinTransactionRepository coinTransactionRepository;
    private final AppUserRepository userRepository;

    public TaskService(TaskTemplateRepository taskTemplateRepository,
                       TaskInstanceRepository taskInstanceRepository,
                       XpTransactionRepository xpTransactionRepository,
                       CoinTransactionRepository coinTransactionRepository,
                       AppUserRepository userRepository) {
        this.taskTemplateRepository = taskTemplateRepository;
        this.taskInstanceRepository = taskInstanceRepository;
        this.xpTransactionRepository = xpTransactionRepository;
        this.coinTransactionRepository = coinTransactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TaskTemplate createTemplate(AppUser user, CreateTaskRequest request) {
        validateTemplate(request);
        TaskTemplate template = new TaskTemplate();
        template.setUser(user);
        template.setTitle(request.title());
        template.setDescription(Optional.ofNullable(request.description()).orElse(""));
        template.setCategory(Optional.ofNullable(request.category()).orElse("Other"));
        template.setPriority(Optional.ofNullable(request.priority()).orElse(Priority.MEDIUM));
        template.setTaskType(request.taskType());
        template.setRecurrenceRule(Optional.ofNullable(request.recurrenceRule()).orElse(RecurrenceRule.NONE));
        template.setScheduledDate(request.scheduledDate());
        template.setXpReward(request.xpReward());
        template.setCoinReward(request.coinReward());
        return taskTemplateRepository.save(template);
    }

    @Transactional
    public TaskTemplate updateTemplate(AppUser user, Long templateId, UpdateTaskRequest request) {
        TaskTemplate template = taskTemplateRepository.findByIdAndUser(templateId, user)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Task template not found"));

        if (request.title() != null) template.setTitle(request.title());
        if (request.description() != null) template.setDescription(request.description());
        if (request.category() != null) template.setCategory(request.category());
        if (request.priority() != null) template.setPriority(request.priority());
        if (request.taskType() != null) template.setTaskType(request.taskType());
        if (request.recurrenceRule() != null) template.setRecurrenceRule(request.recurrenceRule());
        if (request.scheduledDate() != null) template.setScheduledDate(request.scheduledDate());
        if (request.xpReward() != null) template.setXpReward(request.xpReward());
        if (request.coinReward() != null) template.setCoinReward(request.coinReward());

        if (template.getTaskType() == TaskType.ONE_TIME && template.getScheduledDate() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "One-time tasks require scheduledDate");
        }
        if (template.getTaskType() == TaskType.RECURRING && template.getRecurrenceRule() == RecurrenceRule.NONE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Recurring tasks require recurrenceRule");
        }

        return taskTemplateRepository.save(template);
    }

    @Transactional
    public void deleteTemplate(AppUser user, Long templateId) {
        TaskTemplate template = taskTemplateRepository.findByIdAndUser(templateId, user)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Task template not found"));
        template.setActive(false);
        taskTemplateRepository.save(template);
    }

    public List<TaskTemplate> getActiveTemplates(AppUser user) {
        return taskTemplateRepository.findByUserAndActiveTrue(user);
    }

    @Transactional
    public List<TaskInstance> todayTasks(AppUser user) {
        LocalDate today = LocalDate.now(ZoneId.of(user.getTimezone()));
        generateInstancesForDate(user, today);
        return taskInstanceRepository.findByUserAndTaskDateOrderById(user, today);
    }

    @Transactional
    public List<TaskInstance> historyForDate(AppUser user, LocalDate date) {
        generateInstancesForDate(user, date);
        return taskInstanceRepository.findByUserAndTaskDateOrderById(user, date);
    }

    @Transactional
    public TaskInstance completeTask(AppUser user, Long taskInstanceId) {
        TaskInstance instance = taskInstanceRepository.findByIdAndUser(taskInstanceId, user)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Task instance not found"));

        if (instance.getStatus() == TaskStatus.COMPLETED) {
            return instance;
        }
        if (instance.getStatus() == TaskStatus.CANCELLED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cancelled task cannot be completed");
        }

        instance.setStatus(TaskStatus.COMPLETED);
        instance.setCompletedAt(Instant.now());
        taskInstanceRepository.save(instance);

        AppUser lockedUser = userRepository.findByIdForUpdate(user.getId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        try {
            XpTransaction xp = new XpTransaction();
            xp.setUser(lockedUser);
            xp.setAmount(instance.getXpReward());
            xp.setReason("Completed task: " + instance.getTemplate().getTitle());
            xp.setTaskInstance(instance);
            xpTransactionRepository.save(xp);

            CoinTransaction coin = new CoinTransaction();
            coin.setUser(lockedUser);
            coin.setAmount(instance.getCoinReward());
            coin.setTransactionType(CoinTransactionType.EARN);
            coin.setReason("Completed task: " + instance.getTemplate().getTitle());
            coin.setTaskInstance(instance);
            coinTransactionRepository.save(coin);

            lockedUser.addXp(instance.getXpReward());
            lockedUser.addCoins(instance.getCoinReward());
            userRepository.save(lockedUser);
        } catch (DataIntegrityViolationException duplicateAward) {
            // duplicate request raced after status update; reward records are unique per task instance
        }

        return instance;
    }

    @Transactional
    public TaskInstance updateStatus(AppUser user, Long taskInstanceId, TaskStatus status) {
        TaskInstance instance = taskInstanceRepository.findByIdAndUser(taskInstanceId, user)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Task instance not found"));
        if (status == TaskStatus.COMPLETED) {
            return completeTask(user, taskInstanceId);
        }
        instance.setStatus(status);
        if (status != TaskStatus.COMPLETED) {
            instance.setCompletedAt(null);
        }
        return taskInstanceRepository.save(instance);
    }

    @Transactional
    public void generateInstancesForDate(AppUser user, LocalDate date) {
        List<TaskTemplate> templates = taskTemplateRepository.findByUserAndActiveTrue(user);
        for (TaskTemplate template : templates) {
            if (!appliesToDate(template, date)) {
                continue;
            }
            taskInstanceRepository.findByUserAndTemplateAndTaskDate(user, template, date)
                .orElseGet(() -> {
                    TaskInstance instance = new TaskInstance();
                    instance.setUser(user);
                    instance.setTemplate(template);
                    instance.setTaskDate(date);
                    instance.setXpReward(template.getXpReward());
                    instance.setCoinReward(template.getCoinReward());
                    instance.setStatus(TaskStatus.PENDING);
                    return taskInstanceRepository.save(instance);
                });
        }
    }

    private boolean appliesToDate(TaskTemplate template, LocalDate date) {
        if (template.getTaskType() == TaskType.ONE_TIME) {
            return date.equals(template.getScheduledDate());
        }
        if (template.getRecurrenceRule() == RecurrenceRule.DAILY) {
            return true;
        }
        if (template.getRecurrenceRule() == RecurrenceRule.WEEKLY) {
            LocalDate anchor = template.getScheduledDate() != null ? template.getScheduledDate() :
                LocalDate.ofInstant(template.getCreatedAt(), ZoneId.of(template.getUser().getTimezone()));
            return anchor.getDayOfWeek() == date.getDayOfWeek();
        }
        return false;
    }

    private void validateTemplate(CreateTaskRequest request) {
        if (request.taskType() == TaskType.ONE_TIME && request.scheduledDate() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "One-time tasks require scheduledDate");
        }
        if (request.taskType() == TaskType.RECURRING && request.recurrenceRule() == RecurrenceRule.NONE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Recurring tasks require recurrenceRule");
        }
    }

    public record CreateTaskRequest(
        String title,
        String description,
        String category,
        Priority priority,
        TaskType taskType,
        RecurrenceRule recurrenceRule,
        LocalDate scheduledDate,
        int xpReward,
        int coinReward
    ) {}

    public record UpdateTaskRequest(
        String title,
        String description,
        String category,
        Priority priority,
        TaskType taskType,
        RecurrenceRule recurrenceRule,
        LocalDate scheduledDate,
        Integer xpReward,
        Integer coinReward
    ) {}
}
