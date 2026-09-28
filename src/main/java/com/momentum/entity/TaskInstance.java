package com.momentum.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "task_instance", uniqueConstraints = {
    @UniqueConstraint(name = "uk_task_instance_date", columnNames = {"user_id", "template_id", "taskDate"})
})
public class TaskInstance extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private TaskTemplate template;

    private String titleSnapshot;

    private String categorySnapshot;

    @Enumerated(EnumType.STRING)
    private Priority prioritySnapshot;

    @Column(nullable = false)
    private LocalDate taskDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    @Column(nullable = false)
    private int xpReward;

    @Column(nullable = false)
    private int coinReward;

    private Instant completedAt;

    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public TaskTemplate getTemplate() { return template; }
    public void setTemplate(TaskTemplate template) { this.template = template; }
    public String getTitleSnapshot() { return titleSnapshot; }
    public void setTitleSnapshot(String titleSnapshot) { this.titleSnapshot = titleSnapshot; }
    public String getCategorySnapshot() { return categorySnapshot; }
    public void setCategorySnapshot(String categorySnapshot) { this.categorySnapshot = categorySnapshot; }
    public Priority getPrioritySnapshot() { return prioritySnapshot; }
    public void setPrioritySnapshot(Priority prioritySnapshot) { this.prioritySnapshot = prioritySnapshot; }
    public LocalDate getTaskDate() { return taskDate; }
    public void setTaskDate(LocalDate taskDate) { this.taskDate = taskDate; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }
    public int getCoinReward() { return coinReward; }
    public void setCoinReward(int coinReward) { this.coinReward = coinReward; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
