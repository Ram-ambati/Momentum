package com.momentum.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "xp_transaction", indexes = @Index(name = "idx_xp_user_created", columnList = "user_id, createdAt"),
    uniqueConstraints = @UniqueConstraint(name = "uk_xp_task_once", columnNames = "task_instance_id"))
public class XpTransaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser user;

    @Column(nullable = false)
    private int amount;

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
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public TaskInstance getTaskInstance() { return taskInstance; }
    public void setTaskInstance(TaskInstance taskInstance) { this.taskInstance = taskInstance; }
}
