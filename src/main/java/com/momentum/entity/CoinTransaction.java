package com.momentum.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "coin_transaction", indexes = @Index(name = "idx_coin_user_created", columnList = "user_id, createdAt"),
    uniqueConstraints = @UniqueConstraint(name = "uk_coin_task_once", columnNames = "task_instance_id"))
public class CoinTransaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser user;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoinTransactionType transactionType;

    @Column(nullable = false)
    private String reason;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_instance_id")
    private TaskInstance taskInstance;

    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public CoinTransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(CoinTransactionType transactionType) { this.transactionType = transactionType; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public TaskInstance getTaskInstance() { return taskInstance; }
    public void setTaskInstance(TaskInstance taskInstance) { this.taskInstance = taskInstance; }
}
