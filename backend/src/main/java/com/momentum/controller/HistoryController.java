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
import java.time.ZoneId;
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
        LocalDate today = LocalDate.now(userContextService.requireUser().getTimezone() != null 
            ? ZoneId.of(userContextService.requireUser().getTimezone()) 
            : ZoneId.of("UTC"));
        LocalDate actualTo = to.isBefore(today) ? to : today;
        LocalDate cursor = from;
        
        while (!cursor.isAfter(actualTo)) {
            grouped.put(cursor, taskService.historyForDate(userContextService.requireUser(), cursor));
            cursor = cursor.plusDays(1);
        }
        return grouped.entrySet().stream().map(e -> DaySummary.from(e.getKey(), e.getValue())).collect(Collectors.toList());
    }

    @GetMapping("/monthly")
    public MonthlyStats monthlyStats(@RequestParam int year, @RequestParam int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = start.withDayOfMonth(start.lengthOfMonth());
        LocalDate today = LocalDate.now(userContextService.requireUser().getTimezone() != null 
            ? ZoneId.of(userContextService.requireUser().getTimezone()) 
            : ZoneId.of("UTC"));
            
        // Don't query past today!
        LocalDate end = endOfMonth.isBefore(today) ? endOfMonth : today;
        LocalDate cursor = start;
        
        int total = 0;
        int completed = 0;
        int xpEarned = 0;
        int coinsEarned = 0;
        
        while (!cursor.isAfter(end)) {
            List<TaskInstance> day = taskService.historyForDate(userContextService.requireUser(), cursor);
            total += day.size();
            for(TaskInstance i : day) {
                if (i.getStatus() == TaskStatus.COMPLETED) {
                    completed++;
                    xpEarned += i.getXpReward();
                    coinsEarned += i.getCoinReward();
                }
            }
            cursor = cursor.plusDays(1);
        }
        int completionPct = total == 0 ? 0 : (int) ((completed * 100.0) / total);
        return new MonthlyStats(year, month, total, completed, completionPct, xpEarned, coinsEarned);
    }

    public record MonthlyStats(int year, int month, int totalTasks, int completedTasks, int completionPct, int xpEarned, int coinsEarned) {}

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
