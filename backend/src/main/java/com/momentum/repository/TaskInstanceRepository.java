package com.momentum.repository;

import com.momentum.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskInstanceRepository extends JpaRepository<TaskInstance, Long> {
    Optional<TaskInstance> findByUserAndTemplateAndTaskDate(AppUser user, TaskTemplate template, LocalDate date);
    List<TaskInstance> findByUserAndTaskDateOrderById(AppUser user, LocalDate date);
    Optional<TaskInstance> findByIdAndUser(Long id, AppUser user);
    List<TaskInstance> findByUserAndTaskDateBetweenOrderByTaskDateAscIdAsc(AppUser user, LocalDate from, LocalDate to);
    List<TaskInstance> findByUserAndTemplateOrderByTaskDateDesc(AppUser user, TaskTemplate template);
    List<TaskInstance> findByUserAndStatusAndTaskDateBefore(AppUser user, TaskStatus status, LocalDate date);
}
