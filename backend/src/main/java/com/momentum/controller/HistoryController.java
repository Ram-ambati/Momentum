package com.momentum.controller;

import com.momentum.entity.TaskInstance;
import com.momentum.entity.TaskStatus;
import com.momentum.service.TaskService;
import com.momentum.service.UserContextService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/history")
public class HistoryController {
    private final TaskService taskService;
    private final UserContextService userContextService;

    public HistoryController(TaskService taskService, UserContextService userContextService) {
        this.taskService = taskService;
        this.userContextService = userContextService;
    }

    @GetMapping
    public List<DaySummary> dateRange(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        Map<LocalDate, List<TaskInstance>> grouped = new LinkedHashMap<>();
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            grouped.put(cursor, taskService.historyForDate(userContextService.requireUser(), cursor));
            cursor = cursor.plusDays(1);
        }
        return grouped.entrySet().stream().map(e -> DaySummary.from(e.getKey(), e.getValue())).collect(Collectors.toList());
    }

    public record DaySummary(LocalDate date, int total, int completed, int missed, int completionPct, int xpEarned, int coinsEarned, String productivityLabel) {
        static DaySummary from(LocalDate date, List<TaskInstance> instances) {
            int total = instances.size();
            int completed = (int) instances.stream().filter(i -> i.getStatus() == TaskStatus.COMPLETED).count();
            int missed = (int) instances.stream().filter(i -> i.getStatus() == TaskStatus.MISSED).count();
            int xp = instances.stream().filter(i -> i.getStatus() == TaskStatus.COMPLETED).mapToInt(TaskInstance::getXpReward).sum();
            int coins = instances.stream().filter(i -> i.getStatus() == TaskStatus.COMPLETED).mapToInt(TaskInstance::getCoinReward).sum();
            int pct = total == 0 ? 0 : (int) ((completed * 100.0) / total);
            String label = pct >= 90 ? "Excellent" : pct >= 70 ? "Good" : pct >= 40 ? "Average" : (total == 0 ? "No activity" : "Low");
            return new DaySummary(date, total, completed, missed, pct, xp, coins, label);
        }
    }
}
