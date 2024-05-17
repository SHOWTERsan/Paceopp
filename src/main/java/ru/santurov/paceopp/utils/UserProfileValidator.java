package ru.santurov.paceopp.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.santurov.paceopp.DTO.UserProfileDTO;
import ru.santurov.paceopp.models.User;
import ru.santurov.paceopp.services.UserService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserProfileValidator implements Validator {

    private final UserService userService;
    private final PasswordValidator passwordValidator;

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserProfileDTO userProfileDTO = (UserProfileDTO) target;

        Optional<User> existingUser = userService.findByEmail(userProfileDTO.getEmail());

        // Check if there's an existing user with the provided email
        if (existingUser.isPresent()) {
            // If the authenticated user's email is the same as the existing user's email,
            // it means the email belongs to the current user and it's okay to proceed with the update
            User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!authenticatedUser.getEmail().equals(userProfileDTO.getEmail())) {
                // If the email doesn't belong to the authenticated user, reject the update
                errors.rejectValue("email", "", "Человек с таким email уже существует");
            }
        }

        // Validate password if provided
        if (userProfileDTO.getPassword() != null && !userProfileDTO.getPassword().isEmpty()) {
            passwordValidator.validate("password", userProfileDTO.getPassword(), errors);
        }

    }
}

