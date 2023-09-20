package ru.santurov.paceopp.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
    @Size(min = 6,max = 255,message = "Пароль должен содержать от 8 символов до 255 символов.")
    private String newPassword;
   // @NotEmpty(message = "Оба пароля должны быть обязательно заполнены")
    private String confirmPassword;
}
