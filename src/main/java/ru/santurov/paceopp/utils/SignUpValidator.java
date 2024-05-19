package ru.santurov.paceopp.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.santurov.paceopp.DTO.SignupFormDTO;
import ru.santurov.paceopp.services.UserService;

@Component
public class SignUpValidator implements Validator {

    private final UserService userService;
    private final PasswordValidator passwordValidator;

    @Autowired
    public SignUpValidator(UserService userService, PasswordValidator passwordValidator) {
        this.userService = userService;
        this.passwordValidator = passwordValidator;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return SignupFormDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignupFormDTO signupFormDTO =(SignupFormDTO) target;

        passwordValidator.validate("password", signupFormDTO.getPassword(), errors);
        if (userService.findByUsername(signupFormDTO.getUsername()).isPresent())
            errors.rejectValue("username","","Человек с таким именем уже существует");
        if (userService.findByEmail(signupFormDTO.getEmail()).isPresent())
            errors.rejectValue("email","","Человек с таким email уже существует");
    }
}
