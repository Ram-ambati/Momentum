package com.momentum.service;

import com.momentum.entity.AppUser;
import com.momentum.entity.AuthSession;
import com.momentum.exception.ApiException;
import com.momentum.repository.AppUserRepository;
import com.momentum.repository.AuthSessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {
    private final AppUserRepository userRepository;
    private final AuthSessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AppUserRepository userRepository, AuthSessionRepository sessionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        userRepository.findByUsername(request.username()).ifPresent(existing -> {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists");
        });
        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setTimezone(request.timezone() == null ? "UTC" : request.timezone());
        userRepository.save(user);
        return createSession(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return createSession(user);
    }

    @Transactional
    public void logout(String token) {
        sessionRepository.findByTokenAndRevokedFalse(token).ifPresent(session -> {
            session.setRevoked(true);
            sessionRepository.save(session);
        });
    }

    private AuthResponse createSession(AppUser user) {
        AuthSession session = new AuthSession();
        session.setUser(user);
        session.setToken(UUID.randomUUID().toString());
        sessionRepository.save(session);
        return new AuthResponse(session.getToken(), user.getId(), user.getUsername(), user.getTimezone());
    }

    public record RegisterRequest(String username, String password, String timezone) {}
    public record LoginRequest(String username, String password) {}
    public record AuthResponse(String token, Long userId, String username, String timezone) {}
}
