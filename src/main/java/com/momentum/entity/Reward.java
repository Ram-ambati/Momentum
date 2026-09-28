package com.momentum.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reward")
public class Reward extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description = "";

    @Column(nullable = false)
    private int coinCost;

    @Column(nullable = false)
    private boolean active = true;

    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getCoinCost() { return coinCost; }
    public void setCoinCost(int coinCost) { this.coinCost = coinCost; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
