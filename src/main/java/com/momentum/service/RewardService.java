package com.momentum.service;

import com.momentum.entity.*;
import com.momentum.exception.ApiException;
import com.momentum.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RewardService {
    private final RewardRepository rewardRepository;
    private final RewardRedemptionRepository rewardRedemptionRepository;
    private final AppUserRepository userRepository;
    private final CoinTransactionRepository coinTransactionRepository;

    public RewardService(RewardRepository rewardRepository,
                         RewardRedemptionRepository rewardRedemptionRepository,
                         AppUserRepository userRepository,
                         CoinTransactionRepository coinTransactionRepository) {
        this.rewardRepository = rewardRepository;
        this.rewardRedemptionRepository = rewardRedemptionRepository;
        this.userRepository = userRepository;
        this.coinTransactionRepository = coinTransactionRepository;
    }

    @Transactional
    public Reward createReward(AppUser user, CreateRewardRequest request) {
        Reward reward = new Reward();
        reward.setUser(user);
        reward.setName(request.name());
        reward.setDescription(request.description() == null ? "" : request.description());
        reward.setCoinCost(request.coinCost());
        reward.setActive(true);
        return rewardRepository.save(reward);
    }

    public List<Reward> listRewards(AppUser user) {
        return rewardRepository.findByUserOrderByIdDesc(user);
    }

    public List<RewardRedemption> listRedemptions(AppUser user) {
        return rewardRedemptionRepository.findByUserOrderByIdDesc(user);
    }

    @Transactional
    public RewardRedemption redeem(AppUser user, Long rewardId) {
        Reward reward = rewardRepository.findByIdAndUser(rewardId, user)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Reward not found"));
        if (!reward.isActive()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Reward is inactive");
        }

        AppUser lockedUser = userRepository.findByIdForUpdate(user.getId())
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (lockedUser.getCoinBalance() < reward.getCoinCost()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Insufficient coins");
        }

        lockedUser.addCoins(-reward.getCoinCost());
        userRepository.save(lockedUser);

        CoinTransaction spend = new CoinTransaction();
        spend.setUser(lockedUser);
        spend.setAmount(-reward.getCoinCost());
        spend.setReason("Redeemed reward: " + reward.getName());
        spend.setTransactionType(CoinTransactionType.SPEND);
        coinTransactionRepository.save(spend);

        RewardRedemption redemption = new RewardRedemption();
        redemption.setUser(lockedUser);
        redemption.setReward(reward);
        redemption.setCoinCost(reward.getCoinCost());
        return rewardRedemptionRepository.save(redemption);
    }

    public record CreateRewardRequest(String name, String description, int coinCost) {}
}
