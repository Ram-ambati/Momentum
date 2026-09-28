package com.momentum.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_user", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_username", columnNames = "username")
})
public class AppUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String timezone = "UTC";

    @Column(nullable = false)
    private long xpTotal = 0;

    @Column(nullable = false)
    private long coinBalance = 0;

    @Column(nullable = false)
    private long recoveryTokenBalance = 0;

    @Version
    private long version;

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public long getXpTotal() { return xpTotal; }
    public void addXp(long delta) { this.xpTotal += delta; }
    public long getCoinBalance() { return coinBalance; }
    public void addCoins(long delta) { this.coinBalance += delta; }
    public long getRecoveryTokenBalance() { return recoveryTokenBalance; }
    public void addRecoveryTokens(long delta) { this.recoveryTokenBalance += delta; }
}
