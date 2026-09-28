package com.momentum.service;

import com.momentum.config.LevelProperties;
import com.momentum.entity.*;
import com.momentum.repository.TaskInstanceRepository;
import com.momentum.repository.TaskTemplateRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class ProgressService {
    private final LevelProperties levelProperties;
    private final TaskInstanceRepository taskInstanceRepository;
    private final TaskTemplateRepository taskTemplateRepository;

    public ProgressService(LevelProperties levelProperties,
                           TaskInstanceRepository taskInstanceRepository,
                           TaskTemplateRepository taskTemplateRepository) {
        this.levelProperties = levelProperties;
        this.taskInstanceRepository = taskInstanceRepository;
        this.taskTemplateRepository = taskTemplateRepository;
    }

    public ProgressView progress(AppUser user) {
        List<Long> thresholds = levelProperties.getLevelThresholds();
        int level = 1;
        long currentFloor = thresholds.getFirst();
        long next = thresholds.getLast();
        for (int i = 0; i < thresholds.size(); i++) {
            if (user.getXpTotal() >= thresholds.get(i)) {
                level = i + 1;
                currentFloor = thresholds.get(i);
                if (i + 1 < thresholds.size()) {
                    next = thresholds.get(i + 1);
                } else {
                    next = thresholds.get(i) + 500;
                }
            }
        }
        return new ProgressView(level, user.getXpTotal(), currentFloor, next, user.getCoinBalance());
    }

    public StreakView streaks(AppUser user) {
        LocalDate today = LocalDate.now(ZoneId.of(user.getTimezone()));
        int overall = 0;
        LocalDate cursor = today;
        while (true) {
            List<TaskInstance> day = taskInstanceRepository.findByUserAndTaskDateOrderById(user, cursor);
            if (day.isEmpty()) {
                break;
            }
            boolean allDone = day.stream().allMatch(t -> t.getStatus() == TaskStatus.COMPLETED || t.getStatus() == TaskStatus.CANCELLED);
            if (!allDone) break;
            overall++;
            cursor = cursor.minusDays(1);
        }

        Map<Long, Integer> byTemplate = new LinkedHashMap<>();
        for (TaskTemplate template : taskTemplateRepository.findByUserAndActiveTrue(user)) {
            int streak = 0;
            for (TaskInstance instance : taskInstanceRepository.findByUserAndTemplateOrderByTaskDateDesc(user, template)) {
                if (instance.getStatus() == TaskStatus.COMPLETED && (streak == 0 || instance.getTaskDate().equals(today.minusDays(streak)))) {
                    streak++;
                } else if (streak > 0) {
                    break;
                }
            }
            byTemplate.put(template.getId(), streak);
        }
        return new StreakView(overall, byTemplate);
    }

    public record ProgressView(int level, long totalXp, long currentLevelXp, long nextLevelXp, long coinBalance) {}
    public record StreakView(int overallStreak, Map<Long, Integer> taskStreaks) {}
}
