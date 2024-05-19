package ru.santurov.paceopp.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.santurov.paceopp.models.TokenType;
import ru.santurov.paceopp.models.User;
import ru.santurov.paceopp.models.VerificationToken;
import ru.santurov.paceopp.services.TokenService;
import ru.santurov.paceopp.services.UserService;

import java.util.Optional;

@Controller
public class TokenVerificationController {
    private final TokenService tokenService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public TokenVerificationController(TokenService tokenService, UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.tokenService = tokenService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/checkVerification")
    public void verifyUser(String uuid) {
        Optional<User> user = userService.findByUuid(uuid);
        Optional<VerificationToken> optionalToken = tokenService.getTokenByUserAndType(user, TokenType.EMAIL_VERIFICATION);

        String status = "not_verified";
        if (optionalToken.isPresent()) {
            VerificationToken vtoken = optionalToken.get();
            if (user.get().isVerified()) {
                if (vtoken.isExpired()) {
                    status = "expired";
                }
                else {
                    status = "verified";
                }

                tokenService.delete(vtoken);
            }
            else {
                status = "not_verified";
                if (vtoken.isExpired()) {
                    userService.delete(vtoken.getUser());
                    tokenService.delete(vtoken);
                    status = "expired";
                }
            }
        }
        else {
            status = "token_not_found";
        }

        messagingTemplate.convertAndSend("/topic/statusUpdate/"+uuid, status);
    }
}
