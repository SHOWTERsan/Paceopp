package ru.santurov.paceopp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.santurov.paceopp.models.TokenType;
import ru.santurov.paceopp.models.VerificationToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);
    List<VerificationToken> findAllByExpired(boolean b);
    List<VerificationToken> findAllByExpiryDateBefore(LocalDateTime expiryDate);

    void deleteAllByExpiryDateBeforeAndType(LocalDateTime expiryDate, TokenType type);
    List<VerificationToken> findAllByExpiredAndType(boolean b,TokenType type);
    List<VerificationToken> findAllByExpiryDateBeforeAndType(LocalDateTime expiryDate,TokenType type);

    void deleteAllByExpired(boolean b);
}
