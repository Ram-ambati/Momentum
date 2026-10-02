package com.momentum;

import com.momentum.entity.AppUser;
import com.momentum.entity.CoinTransaction;
import com.momentum.entity.CoinTransactionType;
import com.momentum.entity.Reward;
import com.momentum.exception.ApiException;
import com.momentum.repository.AppUserRepository;
import com.momentum.repository.CoinTransactionRepository;
import com.momentum.service.RewardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class RewardRedemptionIntegrityTest {
    @Autowired private RewardService rewardService;
    @Autowired private AppUserRepository userRepository;
    @Autowired private CoinTransactionRepository coinRepository;

    @Test
    void redemptionCannotGoNegative() {
        AppUser user = new AppUser();
        user.setUsername("u2");
        user.setPasswordHash("hash");
        user.setTimezone("UTC");
        user.addCoins(100);
        userRepository.save(user);

        Reward reward = rewardService.createReward(user, new RewardService.CreateRewardRequest("Gaming", "", 120));

        assertThatThrownBy(() -> rewardService.redeem(user, reward.getId()))
            .isInstanceOf(ApiException.class)
            .hasMessageContaining("Insufficient coins");

        assertThat(userRepository.findById(user.getId()).orElseThrow().getCoinBalance()).isEqualTo(100);
    }

    @Test
    void redemptionCreatesSpendTransaction() {
        AppUser user = new AppUser();
        user.setUsername("u3");
        user.setPasswordHash("hash");
        user.setTimezone("UTC");
        user.addCoins(300);
        userRepository.save(user);

        Reward reward = rewardService.createReward(user, new RewardService.CreateRewardRequest("Movie", "", 100));
        rewardService.redeem(user, reward.getId());

        CoinTransaction txn = coinRepository.findByUserOrderByIdDesc(user, org.springframework.data.domain.Pageable.unpaged()).getContent().getFirst();
        assertThat(txn.getTransactionType()).isEqualTo(CoinTransactionType.SPEND);
        assertThat(txn.getAmount()).isEqualTo(-100);
        assertThat(userRepository.findById(user.getId()).orElseThrow().getCoinBalance()).isEqualTo(200);
    }
}
