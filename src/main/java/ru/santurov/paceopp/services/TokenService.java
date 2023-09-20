package ru.santurov.paceopp.services;

import jakarta.transaction.Transactional;
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

    public void deleteAllExpiredByPasswordReset() {
        tokenRepository.deleteAllByExpiryDateBeforeAndType(LocalDateTime.now(), TokenType.PASSWORD_RESET);
    }
    @Transactional
    public Optional<VerificationToken> getTokenByUserAndType(Optional<User> user, TokenType type) {
        return user.flatMap(value -> value.getTokens()
                .stream()
                .filter(p -> p.getType() == type)
                .findFirst());
    }

    public List<VerificationToken> findAllExpiredByEmailVerification() {
        return tokenRepository.findAllByExpiryDateBeforeAndType(LocalDateTime.now(), TokenType.EMAIL_VERIFICATION);
    }

    public List<VerificationToken> findExpiredTokens() {
        return tokenRepository.findAllByExpired(true);
    }
    public List<VerificationToken> findAllByExpiryDateBeforeNow() {
        return tokenRepository.findAllByExpiryDateBefore(LocalDateTime.now());
    }

    public void deleteAllUnverifiedUsersWithExpiredTokens() {
        List<VerificationToken> expiredTokens = tokenRepository.findAllByExpiredAndType(true,TokenType.EMAIL_VERIFICATION);
        List<User> usersToDelete = expiredTokens
                .stream()
                .map(VerificationToken::getUser)
                .filter(user ->!user.isVerified())
                .toList();

        usersToDelete.forEach(userService::delete);
    }

    @Transactional
    public void deleteExpiredTokens() {
        tokenRepository.deleteAllByExpired(true);
    }

    @Transactional
    public void deleteExpiredTokensByTime() {
        tokenRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
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

    public void setExpired(VerificationToken verificationToken) {
        verificationToken.setExpired(true);
        tokenRepository.save(verificationToken);
    }

    public void delete(VerificationToken verificationToken) {
        tokenRepository.delete(verificationToken);
    }
}
