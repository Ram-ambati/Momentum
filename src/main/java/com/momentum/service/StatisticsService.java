package com.momentum.service;

import com.momentum.entity.AppUser;
import com.momentum.entity.TaskInstance;
import com.momentum.entity.TaskStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsService {
    private final TaskService taskService;

    public StatisticsService(TaskService taskService) {
        this.taskService = taskService;
    }

    public DailyStats daily(AppUser user, LocalDate date) {
        return summarize(date, taskService.historyForDate(user, date));
    }

    public WeeklyStats weekly(AppUser user, LocalDate start) {
        LocalDate from = start.with(DayOfWeek.MONDAY);
        LocalDate to = from.plusDays(6);
        List<DailyStats> days = new ArrayList<>();
        for (LocalDate cursor = from; !cursor.isAfter(to); cursor = cursor.plusDays(1)) {
            days.add(daily(user, cursor));
        }

        int total = days.stream().mapToInt(DailyStats::total).sum();
        int completed = days.stream().mapToInt(DailyStats::completed).sum();
        int missed = days.stream().mapToInt(DailyStats::missed).sum();
        int xp = days.stream().mapToInt(DailyStats::xpEarned).sum();
        int coins = days.stream().mapToInt(DailyStats::coinsEarned).sum();
        int completionPct = total == 0 ? 0 : (int) ((completed * 100.0) / total);

        int longestRun = 0;
        int run = 0;
        for (DailyStats day : days) {
            if (day.total() > 0 && day.completionPct() == 100) {
                run++;
                longestRun = Math.max(longestRun, run);
            } else {
                run = 0;
            }
        }

        return new WeeklyStats(from, to, total, completed, missed, completionPct, xp, coins, longestRun, days);
    }

    public MonthlyStats monthly(AppUser user, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        List<DailyStats> days = new ArrayList<>();
        for (int d = 1; d <= ym.lengthOfMonth(); d++) {
            days.add(daily(user, ym.atDay(d)));
        }

        int total = days.stream().mapToInt(DailyStats::total).sum();
        int completed = days.stream().mapToInt(DailyStats::completed).sum();
        int xp = days.stream().mapToInt(DailyStats::xpEarned).sum();
        double avgCompletion = days.isEmpty() ? 0 : days.stream().mapToInt(DailyStats::completionPct).average().orElse(0);
        DailyStats bestDay = days.stream().max((a, b) -> Integer.compare(a.completionPct(), b.completionPct())).orElse(new DailyStats(ym.atDay(1), 0, 0, 0, 0, 0, 0, 0, "No activity"));

        LocalDate today = LocalDate.now(ZoneId.of(user.getTimezone()));
        int currentStreak = 0;
        for (LocalDate cursor = today; !cursor.isBefore(ym.atDay(1)); cursor = cursor.minusDays(1)) {
            DailyStats day = days.get(cursor.getDayOfMonth() - 1);
            if (day.total() > 0 && day.completionPct() == 100) {
                currentStreak++;
            } else {
                break;
            }
        }

        int longestStreak = 0;
        int run = 0;
        for (DailyStats day : days) {
            if (day.total() > 0 && day.completionPct() == 100) {
                run++;
                longestStreak = Math.max(longestStreak, run);
            } else {
                run = 0;
            }
        }

        return new MonthlyStats(year, month, xp, avgCompletion, bestDay.date(), completed, currentStreak, longestStreak, days);
    }

    public List<CalendarDay> calendar(AppUser user, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        List<CalendarDay> result = new ArrayList<>();
        for (int d = 1; d <= ym.lengthOfMonth(); d++) {
            DailyStats day = daily(user, ym.atDay(d));
            result.add(new CalendarDay(day.date(), day.completionPct(), day.productivityLabel(), day.total(), day.completed(), day.missed(), day.xpEarned(), day.coinsEarned()));
        }
        return result;
    }

    public DailyStats summarize(LocalDate date, List<TaskInstance> instances) {
        int total = instances.size();
        int completed = (int) instances.stream().filter(i -> i.getStatus() == TaskStatus.COMPLETED).count();
        int missed = (int) instances.stream().filter(i -> i.getStatus() == TaskStatus.MISSED).count();
        int cancelled = (int) instances.stream().filter(i -> i.getStatus() == TaskStatus.CANCELLED).count();
        int pending = (int) instances.stream().filter(i -> i.getStatus() == TaskStatus.PENDING).count();
        int xp = instances.stream().filter(i -> i.getStatus() == TaskStatus.COMPLETED).mapToInt(TaskInstance::getXpReward).sum();
        int coins = instances.stream().filter(i -> i.getStatus() == TaskStatus.COMPLETED).mapToInt(TaskInstance::getCoinReward).sum();
        int pct = total == 0 ? 0 : (int) ((completed * 100.0) / total);
        String label = pct >= 90 ? "Excellent" : pct >= 70 ? "Good" : pct >= 40 ? "Average" : (total == 0 ? "No activity" : "Low");
        return new DailyStats(date, total, completed, missed, cancelled, pending, pct, xp, coins, label);
    }

    public record DailyStats(LocalDate date, int total, int completed, int missed, int cancelled, int pending, int completionPct, int xpEarned, int coinsEarned, String productivityLabel) {}
    public record WeeklyStats(LocalDate from, LocalDate to, int totalTasks, int completedTasks, int missedTasks, int completionPct, int xpEarned, int coinsEarned, int longestPerfectRun, List<DailyStats> days) {}
    public record MonthlyStats(int year, int month, int totalXp, double averageDailyCompletion, LocalDate bestDay, int totalCompletedTasks, int currentStreak, int longestStreak, List<DailyStats> days) {}
    public record CalendarDay(LocalDate date, int completionPct, String productivityLabel, int totalTasks, int completedTasks, int missedTasks, int xpEarned, int coinsEarned) {}
}
