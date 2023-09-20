package ru.santurov.paceopp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.santurov.paceopp.models.VerificationToken;

import java.util.List;

@Service
public class TokenCleanupService {
    private final TokenService tokenService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public TokenCleanupService(TokenService tokenService, SimpMessagingTemplate messagingTemplate) {
        this.tokenService = tokenService;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredTokens() {
        tokenService.deleteAllUnverifiedUsersWithExpiredTokens();
        tokenService.deleteExpiredTokens();
        tokenService.deleteExpiredTokensByTime();
        List<VerificationToken> expiredTokens = tokenService.findAllExpiredByEmailVerification();

        for (VerificationToken token : expiredTokens) {
            tokenService.setExpired(token);
            messagingTemplate.convertAndSend("/topic/checkVerificationStatus/"+token.getUser().getUuid(), "expired");
        }
    }
}
