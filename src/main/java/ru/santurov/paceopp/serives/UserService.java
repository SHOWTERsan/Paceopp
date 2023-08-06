package ru.santurov.paceopp.serives;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.santurov.paceopp.models.User;
import ru.santurov.paceopp.models.VerificationToken;
import ru.santurov.paceopp.repositories.TokenRepository;
import ru.santurov.paceopp.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    private  final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public UserService(UserRepository userRepository, TokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByUuid(String uuid) {
        return userRepository.findByUuid(uuid);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

}
