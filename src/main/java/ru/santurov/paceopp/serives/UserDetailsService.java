package ru.santurov.paceopp.serives;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.santurov.paceopp.models.User;
import ru.santurov.paceopp.repositories.UserRepository;
import ru.santurov.paceopp.security.UserDetails;

import java.util.Optional;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username.toLowerCase());

        if (user.isEmpty()) throw new UsernameNotFoundException("Пользователь не найден!");
        if (!user.get().isVerified()) throw new DisabledException("Пользователь не подтвержден!");
        return new UserDetails(user.get());

    }

}
