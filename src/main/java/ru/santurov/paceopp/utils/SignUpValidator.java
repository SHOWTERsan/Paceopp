package ru.santurov.paceopp.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.santurov.paceopp.DTO.SignupFormDTO;
import ru.santurov.paceopp.services.UserService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SignUpValidator implements Validator {

    private final UserService userService;

    @Autowired
    public SignUpValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return SignupFormDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignupFormDTO signupFormDTO =(SignupFormDTO) target;
        Pattern pattern = Pattern.compile("(?=.*[a-z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}");
        Matcher matcher = pattern.matcher(signupFormDTO.getPassword());

        if (!matcher.matches()){
            errors.rejectValue("password", "", "Пароль должен содержать не менее 8 символов, включая 1 цифру и 1 спецсимвол");
        }
        if (userService.findByUsername(signupFormDTO.getUsername()).isPresent())
            errors.rejectValue("username","","Человек с таким имене уже существует");
        if (userService.findByEmail(signupFormDTO.getEmail()).isPresent())
            errors.rejectValue("email","","Человек с таким email уже существует");
    }
}
