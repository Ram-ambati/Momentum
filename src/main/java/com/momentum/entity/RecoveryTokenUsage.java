package com.momentum.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "recovery_token_usage")
public class RecoveryTokenUsage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    private TaskTemplate template;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecoveryScope scope;

    @Column(nullable = false)
    private LocalDate recoveryDate;

    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public TaskTemplate getTemplate() { return template; }
    public void setTemplate(TaskTemplate template) { this.template = template; }
    public RecoveryScope getScope() { return scope; }
    public void setScope(RecoveryScope scope) { this.scope = scope; }
    public LocalDate getRecoveryDate() { return recoveryDate; }
    public void setRecoveryDate(LocalDate recoveryDate) { this.recoveryDate = recoveryDate; }
}
