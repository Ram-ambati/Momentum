package com.momentum;

import com.momentum.entity.*;
import com.momentum.exception.ApiException;
import com.momentum.repository.AppUserRepository;
import com.momentum.repository.TaskInstanceRepository;
import com.momentum.repository.TaskTemplateRepository;
import com.momentum.service.ProgressService;
import com.momentum.service.RecoveryTokenService;
import com.momentum.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class TaskLifecycleAndRecoveryIntegrationTest {
    @Autowired private TaskService taskService;
    @Autowired private RecoveryTokenService recoveryTokenService;
    @Autowired private ProgressService progressService;
    @Autowired private AppUserRepository userRepository;
    @Autowired private TaskTemplateRepository templateRepository;
    @Autowired private TaskInstanceRepository instanceRepository;

    @Test
    void templateEditAffectsFutureInstancesNotHistoricalSnapshot() {
        AppUser user = new AppUser();
        user.setUsername("u_lifecycle_1");
        user.setPasswordHash("hash");
        user.setTimezone("UTC");
        userRepository.save(user);

        TaskTemplate template = taskService.createTemplate(user, new TaskService.CreateTaskRequest(
            "DSA Practice", "", "Learning", Priority.MEDIUM, TaskType.RECURRING, RecurrenceRule.DAILY,
            null, 30, 10
        ));

        LocalDate today = LocalDate.now();
        taskService.generateInstancesForDate(user, today);
        TaskInstance todayInstance = instanceRepository.findByUserAndTaskDateOrderById(user, today).getFirst();
        assertThat(todayInstance.getTitleSnapshot()).isEqualTo("DSA Practice");
        assertThat(todayInstance.getXpReward()).isEqualTo(30);

        taskService.updateTemplate(user, template.getId(), new TaskService.UpdateTaskTemplateRequest(
            "DSA Advanced", null, null, null, null, null, null, 50, 15, null
        ));

        TaskInstance reloadedToday = instanceRepository.findById(todayInstance.getId()).orElseThrow();
        assertThat(reloadedToday.getTitleSnapshot()).isEqualTo("DSA Practice");
        assertThat(reloadedToday.getXpReward()).isEqualTo(30);

        LocalDate tomorrow = today.plusDays(1);
        taskService.generateInstancesForDate(user, tomorrow);
        TaskInstance tomorrowInstance = instanceRepository.findByUserAndTaskDateOrderById(user, tomorrow).getFirst();
        assertThat(tomorrowInstance.getTitleSnapshot()).isEqualTo("DSA Advanced");
        assertThat(tomorrowInstance.getXpReward()).isEqualTo(50);
    }

    @Test
    void overallRecoveryTokenPreservesStreakWithoutChangingTaskHistory() {
        AppUser user = new AppUser();
        user.setUsername("u_recovery_1");
        user.setPasswordHash("hash");
        user.setTimezone("UTC");
        userRepository.save(user);

        taskService.createTemplate(user, new TaskService.CreateTaskRequest(
            "Workout", "", "Health", Priority.MEDIUM, TaskType.ONE_TIME, RecurrenceRule.NONE,
            LocalDate.now(), 20, 5
        ));

        LocalDate today = LocalDate.now();
        taskService.generateInstancesForDate(user, today);
        TaskInstance instance = instanceRepository.findByUserAndTaskDateOrderById(user, today).getFirst();
        taskService.updateStatus(user, instance.getId(), TaskStatus.MISSED);

        recoveryTokenService.grant(user, 1, "test");
        recoveryTokenService.use(user, new RecoveryTokenService.UseRecoveryTokenRequest(today, null));

        AppUser reloadedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(reloadedUser.getRecoveryTokenBalance()).isZero();
        assertThat(instanceRepository.findById(instance.getId()).orElseThrow().getStatus()).isEqualTo(TaskStatus.MISSED);
        assertThat(progressService.streaks(reloadedUser).overallStreak()).isEqualTo(1);
    }

    @Test
    void userCannotUpdateAnotherUsersTemplate() {
        AppUser owner = new AppUser();
        owner.setUsername("owner_user");
        owner.setPasswordHash("hash");
        owner.setTimezone("UTC");
        userRepository.save(owner);

        AppUser intruder = new AppUser();
        intruder.setUsername("intruder_user");
        intruder.setPasswordHash("hash");
        intruder.setTimezone("UTC");
        userRepository.save(intruder);

        TaskTemplate template = new TaskTemplate();
        template.setUser(owner);
        template.setTitle("Private");
        template.setTaskType(TaskType.RECURRING);
        template.setRecurrenceRule(RecurrenceRule.DAILY);
        template.setXpReward(10);
        template.setCoinReward(2);
        templateRepository.save(template);

        assertThatThrownBy(() -> taskService.updateTemplate(intruder, template.getId(),
            new TaskService.UpdateTaskTemplateRequest("Hacked", null, null, null, null, null, null, null, null, null)
        )).isInstanceOf(ApiException.class).hasMessageContaining("Task template not found");
    }
}
