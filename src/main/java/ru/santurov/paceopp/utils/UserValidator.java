package ru.santurov.paceopp.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.santurov.paceopp.models.User;
import ru.santurov.paceopp.serives.UserService;

@Component
public class UserValidator implements Validator {

    private final UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user =(User) target;

        if (userService.findByUsername(user.getUsername()).isPresent())
            errors.rejectValue("username","","Человек с таким имене уже существует");
    }
}
