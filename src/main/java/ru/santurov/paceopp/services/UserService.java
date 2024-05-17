package ru.santurov.paceopp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.santurov.paceopp.DTO.UserProfileDTO;
import ru.santurov.paceopp.models.User;
import ru.santurov.paceopp.repositories.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByUuid(String uuid) {
        return userRepository.findByUuidAndFetchTokensEagerly(uuid);
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

    public void updateUserProfile(User user, UserProfileDTO userProfileDTO) {
        user.setUsername(userProfileDTO.getUserName());
        user.setEmail(userProfileDTO.getEmail());
        if (userProfileDTO.getPassword() != null && !userProfileDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userProfileDTO.getPassword()));
        }
        userRepository.save(user);
    }
}
