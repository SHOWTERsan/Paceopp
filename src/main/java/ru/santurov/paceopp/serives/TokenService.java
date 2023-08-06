package ru.santurov.paceopp.serives;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.santurov.paceopp.models.TokenType;
import ru.santurov.paceopp.models.User;
import ru.santurov.paceopp.models.VerificationToken;
import ru.santurov.paceopp.repositories.TokenRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TokenService {
    private final UserService userService;
    private final TokenRepository tokenRepository;

    @Autowired
    public TokenService(UserService userService, TokenRepository tokenRepository) {
        this.userService = userService;
        this.tokenRepository = tokenRepository;
    }

    public Optional<VerificationToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public void deleteAllExpiredTokensByPasswordReset() {
        tokenRepository.deleteAllByExpiryDateBeforeAndType(LocalDateTime.now(), TokenType.PASSWORD_RESET);
    }

    public void deleteAllExpiredTokensByVerification() {
        List<VerificationToken> expiredTokens = tokenRepository.findAllByExpiryDateBeforeAndType(LocalDateTime.now(), TokenType.EMAIL_VERIFICATION);
        List<User> usersToDelete = expiredTokens
                .stream()
                .map(VerificationToken::getUser)
                .filter(user ->!user.isVerified())
                .toList();

        usersToDelete.forEach(userService::delete);
    }

    public String generateToken(User user, TokenType type) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setType(type);
        verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        tokenRepository.save(verificationToken);

        return token;
    }

    public void delete(VerificationToken verificationToken) {
        tokenRepository.delete(verificationToken);
    }
}
