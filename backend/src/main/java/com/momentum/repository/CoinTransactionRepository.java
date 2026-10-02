package com.momentum.repository;

import com.momentum.entity.AppUser;
import com.momentum.entity.CoinTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CoinTransactionRepository extends JpaRepository<CoinTransaction, Long> {
    Page<CoinTransaction> findByUserOrderByIdDesc(AppUser user, Pageable pageable);
}
