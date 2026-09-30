package com.momentum.security;

import com.momentum.entity.AuthSession;
import com.momentum.repository.AuthSessionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthSessionRepository sessionRepository;

    public TokenAuthenticationFilter(AuthSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            sessionRepository.findByTokenAndRevokedFalse(token).ifPresent(this::authenticate);
        }
        filterChain.doFilter(request, response);
    }

    private void authenticate(AuthSession session) {
        CurrentUser principal = new CurrentUser(session.getUser().getId(), session.getUser().getUsername());
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(principal, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
