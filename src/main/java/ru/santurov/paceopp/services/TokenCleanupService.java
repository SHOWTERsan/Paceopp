package ru.santurov.paceopp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TokenCleanupService {
    private final TokenService tokenService;

    @Autowired
    public TokenCleanupService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredTokens() {
        tokenService.deleteExpiredTokens();
        tokenService.deleteAllUsersByVerification();
    }
}
