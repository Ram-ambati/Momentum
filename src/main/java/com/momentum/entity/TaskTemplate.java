package com.momentum.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "task_template")
public class TaskTemplate extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description = "";

    @Column(nullable = false)
    private String category = "Other";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecurrenceRule recurrenceRule = RecurrenceRule.NONE;

    private LocalDate scheduledDate;

    @Column(nullable = false)
    private int xpReward;

    @Column(nullable = false)
    private int coinReward;

    @Column(nullable = false)
    private boolean active = true;

    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public TaskType getTaskType() { return taskType; }
    public void setTaskType(TaskType taskType) { this.taskType = taskType; }
    public RecurrenceRule getRecurrenceRule() { return recurrenceRule; }
    public void setRecurrenceRule(RecurrenceRule recurrenceRule) { this.recurrenceRule = recurrenceRule; }
    public LocalDate getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDate scheduledDate) { this.scheduledDate = scheduledDate; }
    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }
    public int getCoinReward() { return coinReward; }
    public void setCoinReward(int coinReward) { this.coinReward = coinReward; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
