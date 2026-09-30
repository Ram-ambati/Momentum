package com.momentum.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;

import java.time.Instant;

@MappedSuperclass
public abstract class BaseEntity {
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
