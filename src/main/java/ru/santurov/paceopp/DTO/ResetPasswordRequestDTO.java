package ru.santurov.paceopp.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
    @NotEmpty(message = "Почту нужно ввести обязательно")
    @Email(message = "Неверный формат почты")
    private String email;
}
