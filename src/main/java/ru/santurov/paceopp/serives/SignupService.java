package ru.santurov.paceopp.serives;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.santurov.paceopp.models.User;
import ru.santurov.paceopp.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SignupService {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public SignupService(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    public void createUser(User user) {
        if (user.getPassword().isEmpty() || user.getPassword().length() < 6 || user.getPassword().length() > 255)
            userService.save(user);
        else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole("ROLE_USER");
            user.setUuid(UUID.randomUUID().toString());
            user.setUsername(user.getUsername().toLowerCase());
            userService.save(user);
        }
    }
}
