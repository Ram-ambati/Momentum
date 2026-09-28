package com.momentum.controller;

import com.momentum.service.RecoveryTokenService;
import com.momentum.service.UserContextService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/streaks/recovery-tokens")
public class RecoveryTokenController {
    private final RecoveryTokenService recoveryTokenService;
    private final UserContextService userContextService;

    public RecoveryTokenController(RecoveryTokenService recoveryTokenService, UserContextService userContextService) {
        this.recoveryTokenService = recoveryTokenService;
        this.userContextService = userContextService;
    }

    @GetMapping
    public RecoveryTokenService.RecoveryTokenView balanceAndHistory() {
        return recoveryTokenService.get(userContextService.requireUser());
    }

    @PostMapping("/grant")
    public RecoveryTokenService.RecoveryTokenView grant(@RequestBody @Valid GrantRecoveryBody body) {
        return recoveryTokenService.grant(userContextService.requireUser(), body.amount(), body.reason());
    }

    @PostMapping("/use")
    public RecoveryTokenService.RecoveryTokenView use(@RequestBody @Valid UseRecoveryBody body) {
        return recoveryTokenService.use(userContextService.requireUser(), new RecoveryTokenService.UseRecoveryTokenRequest(body.date(), body.templateId()));
    }

    public record GrantRecoveryBody(@Min(1) int amount, String reason) {}
    public record UseRecoveryBody(@NotNull LocalDate date, Long templateId) {}
}
