package com.momentum.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "recovery_token_transaction", indexes = @Index(name = "idx_recovery_token_user_created", columnList = "user_id, createdAt"))
public class RecoveryTokenTransaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser user;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecoveryTokenTransactionType transactionType;

    @Column(nullable = false)
    private String reason;

    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public RecoveryTokenTransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(RecoveryTokenTransactionType transactionType) { this.transactionType = transactionType; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
