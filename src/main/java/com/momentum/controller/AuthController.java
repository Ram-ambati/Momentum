package com.momentum.controller;

import com.momentum.entity.AppUser;
import com.momentum.security.CurrentUser;
import com.momentum.service.AuthService;
import com.momentum.service.UserContextService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UserContextService userContextService;

    public AuthController(AuthService authService, UserContextService userContextService) {
        this.authService = authService;
        this.userContextService = userContextService;
    }

    @PostMapping("/register")
    public AuthService.AuthResponse register(@RequestBody @Valid RegisterBody body) {
        return authService.register(new AuthService.RegisterRequest(body.username(), body.password(), body.timezone()));
    }

    @PostMapping("/login")
    public AuthService.AuthResponse login(@RequestBody @Valid LoginBody body) {
        return authService.login(new AuthService.LoginRequest(body.username(), body.password()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(name = "Authorization", required = false) String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            authService.logout(authorization.substring(7));
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {
        CurrentUser currentUser = (CurrentUser) auth.getPrincipal();
        AppUser user = userContextService.requireUser();
        return Map.of("id", currentUser.id(), "username", currentUser.username(), "timezone", user.getTimezone());
    }

    public record RegisterBody(@NotBlank String username, @NotBlank String password, String timezone) {}
    public record LoginBody(@NotBlank String username, @NotBlank String password) {}
}
