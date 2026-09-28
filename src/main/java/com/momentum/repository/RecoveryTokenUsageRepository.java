package com.momentum.repository;

import com.momentum.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecoveryTokenUsageRepository extends JpaRepository<RecoveryTokenUsage, Long> {
    boolean existsByUserAndScopeAndRecoveryDate(AppUser user, RecoveryScope scope, LocalDate recoveryDate);
    boolean existsByUserAndTemplateAndRecoveryDate(AppUser user, TaskTemplate template, LocalDate recoveryDate);
    List<RecoveryTokenUsage> findByUserAndRecoveryDate(AppUser user, LocalDate recoveryDate);
}
