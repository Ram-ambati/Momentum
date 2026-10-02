package com.momentum;

import com.momentum.entity.*;
import com.momentum.repository.*;
import com.momentum.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TaskCompletionIntegrityTest {
    @Autowired private TaskService taskService;
    @Autowired private AppUserRepository userRepository;
    @Autowired private TaskTemplateRepository templateRepository;
    @Autowired private TaskInstanceRepository instanceRepository;
    @Autowired private XpTransactionRepository xpRepository;
    @Autowired private CoinTransactionRepository coinRepository;

    @Test
    void completingTwiceAwardsOnce() {
        AppUser user = new AppUser();
        user.setUsername("u1");
        user.setPasswordHash("hash");
        user.setTimezone("UTC");
        userRepository.save(user);

        TaskTemplate template = new TaskTemplate();
        template.setUser(user);
        template.setTitle("DSA");
        template.setTaskType(TaskType.ONE_TIME);
        template.setRecurrenceRule(RecurrenceRule.NONE);
        template.setScheduledDate(LocalDate.now());
        template.setXpReward(30);
        template.setCoinReward(10);
        templateRepository.save(template);

        taskService.generateInstancesForDate(user, LocalDate.now());
        TaskInstance instance = instanceRepository.findByUserAndTaskDateOrderById(user, LocalDate.now()).getFirst();

        taskService.completeTask(user, instance.getId());
        taskService.completeTask(user, instance.getId());

        assertThat(xpRepository.findByUserOrderByIdDesc(user, org.springframework.data.domain.Pageable.unpaged()).getContent()).hasSize(1);
        assertThat(coinRepository.findByUserOrderByIdDesc(user, org.springframework.data.domain.Pageable.unpaged()).getContent()).hasSize(1);
        assertThat(userRepository.findById(user.getId()).orElseThrow().getXpTotal()).isEqualTo(30);
        assertThat(userRepository.findById(user.getId()).orElseThrow().getCoinBalance()).isEqualTo(10);
    }
}
