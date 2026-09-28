package com.momentum.repository;

import com.momentum.entity.AppUser;
import com.momentum.entity.RewardRedemption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardRedemptionRepository extends JpaRepository<RewardRedemption, Long> {
    List<RewardRedemption> findByUserOrderByIdDesc(AppUser user);
}
