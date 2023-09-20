package ru.santurov.paceopp.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.santurov.paceopp.DTO.ResetPasswordRequestDTO;

@Component
public class ResetPasswordValidator implements Validator {
    private final PasswordValidator passwordValidator;

    @Autowired
    public ResetPasswordValidator(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ResetPasswordRequestDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ResetPasswordRequestDTO resetPasswordRequestDTO = (ResetPasswordRequestDTO) target;

        passwordValidator.validate("newPassword", resetPasswordRequestDTO.getNewPassword(), errors);
        if (!resetPasswordRequestDTO.getNewPassword().equals(resetPasswordRequestDTO.getConfirmPassword())){
            errors.rejectValue("confirmPassword","","Пароли не совпадают");
        }
    }
}
