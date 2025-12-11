package ru.khkhlv.messenger.util;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.khkhlv.messenger.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class SecurityContextHelper {
    private final UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(SecurityContextHelper.class);

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("Current authentication: {}", authentication);
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("No authentication found or not authenticated");
            throw new RuntimeException("Not authenticated");
        }
        String email = authentication.getName();
        log.debug("Authentication name (email): {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found by email: {}", email);
                    return new RuntimeException("User not found");
                })
                .getId();
    }
}