package com.momentum.repository;

import com.momentum.entity.AuthSession;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {
    @EntityGraph(attributePaths = "user")
    Optional<AuthSession> findByTokenAndRevokedFalse(String token);
}
