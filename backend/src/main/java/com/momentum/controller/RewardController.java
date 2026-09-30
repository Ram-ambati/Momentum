package com.momentum.controller;

import com.momentum.entity.Reward;
import com.momentum.entity.RewardRedemption;
import com.momentum.service.RewardService;
import com.momentum.service.UserContextService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {
    private final RewardService rewardService;
    private final UserContextService userContextService;

    public RewardController(RewardService rewardService, UserContextService userContextService) {
        this.rewardService = rewardService;
        this.userContextService = userContextService;
    }

    @GetMapping
    public List<RewardResponse> list() {
        return rewardService.listRewards(userContextService.requireUser()).stream().map(RewardResponse::from).toList();
    }

    @PostMapping
    public RewardResponse create(@RequestBody @Valid CreateRewardBody body) {
        Reward reward = rewardService.createReward(userContextService.requireUser(), new RewardService.CreateRewardRequest(body.name(), body.description(), body.coinCost()));
        return RewardResponse.from(reward);
    }

    @PostMapping("/{id}/redeem")
    public RedemptionResponse redeem(@PathVariable Long id) {
        return RedemptionResponse.from(rewardService.redeem(userContextService.requireUser(), id));
    }

    @GetMapping("/redemptions")
    public List<RedemptionResponse> redemptions() {
        return rewardService.listRedemptions(userContextService.requireUser()).stream().map(RedemptionResponse::from).toList();
    }

    @PatchMapping("/{id}")
    public RewardResponse update(@PathVariable Long id, @RequestBody @Valid UpdateRewardBody body) {
        Reward reward = rewardService.updateReward(userContextService.requireUser(), id, new RewardService.UpdateRewardRequest(body.name(), body.description(), body.coinCost(), body.active()));
        return RewardResponse.from(reward);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        rewardService.deleteReward(userContextService.requireUser(), id);
    }

    public record CreateRewardBody(@NotBlank String name, String description, @Min(1) int coinCost) {}
    public record UpdateRewardBody(String name, String description, @Min(1) Integer coinCost, Boolean active) {}
    public record RewardResponse(Long id, String name, String description, int coinCost, boolean active) {
        static RewardResponse from(Reward reward) {
            return new RewardResponse(reward.getId(), reward.getName(), reward.getDescription(), reward.getCoinCost(), reward.isActive());
        }
    }
    public record RedemptionResponse(Long id, Long rewardId, int coinCost, Instant redeemedAt) {
        static RedemptionResponse from(RewardRedemption redemption) {
            return new RedemptionResponse(redemption.getId(), redemption.getReward().getId(), redemption.getCoinCost(), redemption.getRedeemedAt());
        }
    }
}
