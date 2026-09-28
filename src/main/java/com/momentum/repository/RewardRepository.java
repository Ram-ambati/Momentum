package com.momentum.repository;

import com.momentum.entity.AppUser;
import com.momentum.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findByUserOrderByIdDesc(AppUser user);
    Optional<Reward> findByIdAndUser(Long id, AppUser user);
}
