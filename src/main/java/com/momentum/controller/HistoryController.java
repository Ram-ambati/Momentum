package com.momentum.controller;

import com.momentum.entity.TaskInstance;
import com.momentum.service.StatisticsService;
import com.momentum.service.TaskService;
import com.momentum.service.UserContextService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.time.YearMonth;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/history")
public class HistoryController {
    private final TaskService taskService;
    private final UserContextService userContextService;
    private final StatisticsService statisticsService;

    public HistoryController(TaskService taskService, UserContextService userContextService, StatisticsService statisticsService) {
        this.taskService = taskService;
        this.userContextService = userContextService;
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public List<DaySummary> dateRange(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        Map<LocalDate, List<TaskInstance>> grouped = new LinkedHashMap<>();
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            grouped.put(cursor, taskService.historyForDate(userContextService.requireUser(), cursor));
            cursor = cursor.plusDays(1);
        }
        return grouped.entrySet().stream()
            .map(e -> DaySummary.from(statisticsService.summarize(e.getKey(), e.getValue())))
            .toList();
    }

    @GetMapping("/calendar")
    public List<StatisticsService.CalendarDay> calendar(@RequestParam(required = false) Integer year,
                                                        @RequestParam(required = false) Integer month) {
        var user = userContextService.requireUser();
        LocalDate now = LocalDate.now(ZoneId.of(user.getTimezone()));
        int effectiveYear = year != null ? year : now.getYear();
        int effectiveMonth = month != null ? month : now.getMonthValue();
        YearMonth.of(effectiveYear, effectiveMonth);
        return statisticsService.calendar(user, effectiveYear, effectiveMonth);
    }

    public record DaySummary(LocalDate date, int total, int completed, int missed, int completionPct, int xpEarned, int coinsEarned, String productivityLabel) {
        static DaySummary from(StatisticsService.DailyStats stats) {
            return new DaySummary(
                stats.date(),
                stats.total(),
                stats.completed(),
                stats.missed(),
                stats.completionPct(),
                stats.xpEarned(),
                stats.coinsEarned(),
                stats.productivityLabel()
            );
        }
    }
}
