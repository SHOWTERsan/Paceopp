package ru.santurov.paceopp.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import ru.santurov.paceopp.models.VerificationToken;
import ru.santurov.paceopp.services.TokenService;
import ru.santurov.paceopp.services.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class TokenVerificationController {
    private final TokenService tokenService;
    private final UserService userService;

    public TokenVerificationController(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @MessageMapping("/checkVerification")
    @SendTo("/topic/statusUpdate")
    public String verifyToken(String token) {
        Optional<VerificationToken> optionalToken = tokenService.findByToken(token);

        String status = "not_verified";
        if (optionalToken.isPresent()) {
            VerificationToken vtoken = optionalToken.get();
            if (vtoken.getExpiryDate().isBefore(LocalDateTime.now())) {
                userService.delete(vtoken.getUser());
                status = "expired";
            }
            else if (vtoken.getUser().isVerified()) {
                status = "verified";
            }
        }
        else {
            status = "token_not_found";
        }

        return status;
    }
}
