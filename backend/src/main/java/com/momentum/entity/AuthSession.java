package com.momentum.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "auth_session", indexes = @Index(name = "idx_auth_token", columnList = "token", unique = true))
public class AuthSession extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private boolean revoked = false;

    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }
}
