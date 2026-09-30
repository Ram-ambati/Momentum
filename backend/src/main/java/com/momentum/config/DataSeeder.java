package com.momentum.config;

import com.momentum.entity.*;
import com.momentum.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;

@Configuration
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final TaskTemplateRepository templateRepository;
    private final TaskInstanceRepository instanceRepository;

    public DataSeeder(
            AppUserRepository userRepository,
            TaskTemplateRepository templateRepository,
            TaskInstanceRepository instanceRepository
    ) {
        this.userRepository = userRepository;
        this.templateRepository = templateRepository;
        this.instanceRepository = instanceRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {

        // Wait until a user exists.
        if (userRepository.count() == 0) {
            return;
        }

        // Use the first registered user for development/demo data.
        AppUser user = userRepository.findAll().getFirst();

        // Removed the check preventing seeding so it can insert the missing dashboard data.

        System.out.println("SEEDING MOMENTUM DATA FOR USER: " + user.getUsername());

        /*
         * ---------------------------------------------------------
         * 1. USER PROGRESSION
         * ---------------------------------------------------------
         */

        user.addXp(1850);
        user.addCoins(320);

        userRepository.save(user);

        /*
         * ---------------------------------------------------------
         * 2. DAILY TASK TEMPLATES
         * ---------------------------------------------------------
         */

        // DSA
        TaskTemplate dsa = new TaskTemplate();
        dsa.setUser(user);
        dsa.setTitle("Solve 2 LeetCode Problems");
        dsa.setCategory("DSA");
        dsa.setPriority(Priority.HIGH);
        dsa.setTaskType(TaskType.RECURRING);
        dsa.setRecurrenceRule(RecurrenceRule.DAILY);
        dsa.setXpReward(50);
        dsa.setCoinReward(20);
        templateRepository.save(dsa);

        // Backend
        TaskTemplate backend = new TaskTemplate();
        backend.setUser(user);
        backend.setTitle("Backend From First Principles");
        backend.setCategory("Backend");
        backend.setPriority(Priority.HIGH);
        backend.setTaskType(TaskType.RECURRING);
        backend.setRecurrenceRule(RecurrenceRule.DAILY);
        backend.setXpReward(50);
        backend.setCoinReward(20);
        templateRepository.save(backend);

        // College
        TaskTemplate college = new TaskTemplate();
        college.setUser(user);
        college.setTitle("Complete College Work");
        college.setCategory("College");
        college.setPriority(Priority.MEDIUM);
        college.setTaskType(TaskType.RECURRING);
        college.setRecurrenceRule(RecurrenceRule.DAILY);
        college.setXpReward(30);
        college.setCoinReward(10);
        templateRepository.save(college);

        // Exercise
        TaskTemplate exercise = new TaskTemplate();
        exercise.setUser(user);
        exercise.setTitle("Exercise");
        exercise.setCategory("Health");
        exercise.setPriority(Priority.MEDIUM);
        exercise.setTaskType(TaskType.RECURRING);
        exercise.setRecurrenceRule(RecurrenceRule.DAILY);
        exercise.setXpReward(30);
        exercise.setCoinReward(10);
        templateRepository.save(exercise);

        // Planning
        TaskTemplate planning = new TaskTemplate();
        planning.setUser(user);
        planning.setTitle("Plan Tomorrow");
        planning.setCategory("Personal");
        planning.setPriority(Priority.LOW);
        planning.setTaskType(TaskType.RECURRING);
        planning.setRecurrenceRule(RecurrenceRule.DAILY);
        planning.setXpReward(20);
        planning.setCoinReward(5);
        templateRepository.save(planning);

        /*
         * ---------------------------------------------------------
         * 3. HISTORICAL TASK INSTANCES
         * ---------------------------------------------------------
         *
         * Create enough history for:
         * - Dashboard
         * - Calendar
         * - Streak
         * - Statistics
         * - Completion history
         *
         * Today remains pending so the dashboard has active tasks.
         */

        LocalDate today = LocalDate.now(ZoneId.of("UTC"));

        for (int daysAgo = 14; daysAgo >= 0; daysAgo--) {

            LocalDate taskDate = today.minusDays(daysAgo);

            /*
             * DSA
             *
             * Completed every previous day.
             * Today remains pending.
             */
            createInstance(
                    user,
                    dsa,
                    taskDate,
                    daysAgo == 0
                            ? TaskStatus.PENDING
                            : TaskStatus.COMPLETED
            );

            /*
             * Backend
             *
             * Completed most days.
             * Missed a couple of days to make the history
             * visually meaningful.
             */
            TaskStatus backendStatus;

            if (daysAgo == 0) {
                backendStatus = TaskStatus.PENDING;
            } else if (daysAgo == 5 || daysAgo == 10) {
                backendStatus = TaskStatus.CANCELLED;
            } else {
                backendStatus = TaskStatus.COMPLETED;
            }

            createInstance(
                    user,
                    backend,
                    taskDate,
                    backendStatus
            );

            /*
             * College
             *
             * Mostly completed, with one missed day.
             */
            TaskStatus collegeStatus;

            if (daysAgo == 0) {
                collegeStatus = TaskStatus.PENDING;
            } else if (daysAgo == 7) {
                collegeStatus = TaskStatus.CANCELLED;
            } else {
                collegeStatus = TaskStatus.COMPLETED;
            }

            createInstance(
                    user,
                    college,
                    taskDate,
                    collegeStatus
            );

            /*
             * Exercise
             *
             * Less consistent intentionally so that
             * statistics/heatmaps have variation.
             */
            TaskStatus exerciseStatus;

            if (daysAgo == 0) {
                exerciseStatus = TaskStatus.PENDING;
            } else if (daysAgo == 3 || daysAgo == 8) {
                exerciseStatus = TaskStatus.CANCELLED;
            } else {
                exerciseStatus = TaskStatus.COMPLETED;
            }

            createInstance(
                    user,
                    exercise,
                    taskDate,
                    exerciseStatus
            );

            /*
             * Planning
             *
             * Completed every previous day.
             */
            createInstance(
                    user,
                    planning,
                    taskDate,
                    daysAgo == 0
                            ? TaskStatus.PENDING
                            : TaskStatus.COMPLETED
            );
        }

        System.out.println("MOMENTUM SEEDING COMPLETE.");
    }

    /**
     * Creates a historical TaskInstance using the values
     * from the current TaskTemplate.
     */
    private void createInstance(
            AppUser user,
            TaskTemplate template,
            LocalDate taskDate,
            TaskStatus status
    ) {
        TaskInstance instance = new TaskInstance();

        instance.setUser(user);
        instance.setTemplate(template);
        instance.setTaskDate(taskDate);

        /*
         * Snapshot values are not currently supported by TaskInstance entity.
         * Wait until the entity is updated to use snapshot fields.
         */
        instance.setXpReward(template.getXpReward());
        instance.setCoinReward(template.getCoinReward());

        instance.setStatus(status);

        instanceRepository.save(instance);
    }
}