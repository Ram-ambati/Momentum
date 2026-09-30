package com.momentum.repository;

import com.momentum.entity.AppUser;
import com.momentum.entity.CoinTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoinTransactionRepository extends JpaRepository<CoinTransaction, Long> {
    List<CoinTransaction> findByUserOrderByIdDesc(AppUser user);
}
