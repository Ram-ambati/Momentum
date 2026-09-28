package com.momentum.controller;

import com.momentum.entity.AppUser;
import com.momentum.service.StatisticsService;
import com.momentum.service.UserContextService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/stats")
public class StatisticsController {
    private final StatisticsService statisticsService;
    private final UserContextService userContextService;

    public StatisticsController(StatisticsService statisticsService, UserContextService userContextService) {
        this.statisticsService = statisticsService;
        this.userContextService = userContextService;
    }

    @GetMapping("/daily")
    public StatisticsService.DailyStats daily(@RequestParam(required = false) LocalDate date) {
        AppUser user = userContextService.requireUser();
        LocalDate effectiveDate = date != null ? date : LocalDate.now(ZoneId.of(user.getTimezone()));
        return statisticsService.daily(user, effectiveDate);
    }

    @GetMapping("/weekly")
    public StatisticsService.WeeklyStats weekly(@RequestParam(required = false) LocalDate start) {
        AppUser user = userContextService.requireUser();
        LocalDate effectiveStart = start != null ? start : LocalDate.now(ZoneId.of(user.getTimezone()));
        return statisticsService.weekly(user, effectiveStart);
    }

    @GetMapping("/monthly")
    public StatisticsService.MonthlyStats monthly(@RequestParam(required = false) Integer year,
                                                  @RequestParam(required = false) Integer month) {
        AppUser user = userContextService.requireUser();
        LocalDate now = LocalDate.now(ZoneId.of(user.getTimezone()));
        int effectiveYear = year != null ? year : now.getYear();
        int effectiveMonth = month != null ? month : now.getMonthValue();
        YearMonth.of(effectiveYear, effectiveMonth);
        return statisticsService.monthly(user, effectiveYear, effectiveMonth);
    }
}
