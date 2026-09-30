package com.momentum.service;

import com.momentum.entity.AppUser;
import com.momentum.entity.TaskInstance;
import com.momentum.entity.TaskStatus;
import com.momentum.repository.AppUserRepository;
import com.momentum.repository.TaskInstanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class TaskSchedulerService {
    private static final Logger log = LoggerFactory.getLogger(TaskSchedulerService.class);
    
    private final AppUserRepository userRepository;
    private final TaskInstanceRepository taskInstanceRepository;

    public TaskSchedulerService(AppUserRepository userRepository, TaskInstanceRepository taskInstanceRepository) {
        this.userRepository = userRepository;
        this.taskInstanceRepository = taskInstanceRepository;
    }

    // Run every hour
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void markMissedTasks() {
        log.info("Running scheduled job to mark missed tasks");
        List<AppUser> users = userRepository.findAll();
        for (AppUser user : users) {
            LocalDate today = LocalDate.now(ZoneId.of(user.getTimezone()));
            
            List<TaskInstance> pendingTasks = taskInstanceRepository.findByUserAndStatusAndTaskDateBefore(
                user, TaskStatus.PENDING, today);
                
            for (TaskInstance task : pendingTasks) {
                task.setStatus(TaskStatus.MISSED);
                log.info("Marked task instance {} as MISSED for user {}", task.getId(), user.getUsername());
            }
            if (!pendingTasks.isEmpty()) {
                taskInstanceRepository.saveAll(pendingTasks);
            }
        }
    }
}
