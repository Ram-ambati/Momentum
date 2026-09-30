package com.momentum.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "reward_redemption")
public class RewardRedemption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Reward reward;

    @Column(nullable = false)
    private int coinCost;

    @Column(nullable = false, updatable = false)
    private Instant redeemedAt;

    @PrePersist
    void prePersist() { redeemedAt = Instant.now(); }

    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public Reward getReward() { return reward; }
    public void setReward(Reward reward) { this.reward = reward; }
    public int getCoinCost() { return coinCost; }
    public void setCoinCost(int coinCost) { this.coinCost = coinCost; }
    public Instant getRedeemedAt() { return redeemedAt; }
}
