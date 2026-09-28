package com.momentum.repository;

import com.momentum.entity.AppUser;
import com.momentum.entity.TaskTemplate;
import com.momentum.entity.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskTemplateRepository extends JpaRepository<TaskTemplate, Long> {
    List<TaskTemplate> findByUserAndActiveTrue(AppUser user);
    List<TaskTemplate> findByUserOrderByIdDesc(AppUser user);
    List<TaskTemplate> findByUserAndTaskTypeAndActiveTrueAndScheduledDate(AppUser user, TaskType taskType, LocalDate date);
    Optional<TaskTemplate> findByIdAndUser(Long id, AppUser user);
}
