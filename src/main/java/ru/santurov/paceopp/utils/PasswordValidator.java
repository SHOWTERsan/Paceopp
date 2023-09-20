package ru.santurov.paceopp.utils;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PasswordValidator {
    public void validate(String fieldName, String password, Errors errors) {
        Pattern pattern = Pattern.compile("(?=.*[a-z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}");
        Matcher matcher = pattern.matcher(password);
        if (!matcher.matches()){
            errors.rejectValue(fieldName, "", "Пароль должен содержать не менее 8 символов, включая 1 цифру и 1 спецсимвол");
        }
    }
}
