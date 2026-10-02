package com.momentum.controller;

import com.momentum.entity.CoinTransaction;
import com.momentum.entity.XpTransaction;
import com.momentum.repository.CoinTransactionRepository;
import com.momentum.repository.XpTransactionRepository;
import com.momentum.service.ProgressService;
import com.momentum.service.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProgressController {
    private final ProgressService progressService;
    private final UserContextService userContextService;
    private final XpTransactionRepository xpTransactionRepository;
    private final CoinTransactionRepository coinTransactionRepository;

    public ProgressController(ProgressService progressService,
                              UserContextService userContextService,
                              XpTransactionRepository xpTransactionRepository,
                              CoinTransactionRepository coinTransactionRepository) {
        this.progressService = progressService;
        this.userContextService = userContextService;
        this.xpTransactionRepository = xpTransactionRepository;
        this.coinTransactionRepository = coinTransactionRepository;
    }

    @GetMapping("/progress")
    public ProgressService.ProgressView progress() {
        return progressService.progress(userContextService.requireUser());
    }

    @GetMapping("/streaks")
    public ProgressService.StreakView streaks() {
        return progressService.streaks(userContextService.requireUser());
    }

    @GetMapping("/transactions/xp")
    public Page<XpTxnRow> xpLedger(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return xpTransactionRepository.findByUserOrderByIdDesc(userContextService.requireUser(), PageRequest.of(page, size)).map(XpTxnRow::from);
    }

    @GetMapping("/transactions/coins")
    public Page<CoinTxnRow> coinLedger(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return coinTransactionRepository.findByUserOrderByIdDesc(userContextService.requireUser(), PageRequest.of(page, size)).map(CoinTxnRow::from);
    }

    public record XpTxnRow(Long id, int amount, String reason, Instant createdAt) {
        static XpTxnRow from(XpTransaction x) {
            return new XpTxnRow(x.getId(), x.getAmount(), x.getReason(), x.getCreatedAt());
        }
    }

    public record CoinTxnRow(Long id, int amount, String type, String reason, Instant createdAt) {
        static CoinTxnRow from(CoinTransaction c) {
            return new CoinTxnRow(c.getId(), c.getAmount(), c.getTransactionType().name(), c.getReason(), c.getCreatedAt());
        }
    }
}
