package com.momentum.service;

import com.momentum.entity.AppUser;
import com.momentum.exception.ApiException;
import com.momentum.repository.AppUserRepository;
import com.momentum.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserContextService {
    private final AppUserRepository userRepository;

    public UserContextService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser requireUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CurrentUser currentUser)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return userRepository.findById(currentUser.id())
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }
}
