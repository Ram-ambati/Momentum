package com.momentum.repository;

import com.momentum.entity.AppUser;
import com.momentum.entity.RecoveryTokenTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecoveryTokenTransactionRepository extends JpaRepository<RecoveryTokenTransaction, Long> {
    List<RecoveryTokenTransaction> findByUserOrderByIdDesc(AppUser user);
}
