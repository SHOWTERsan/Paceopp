package ru.santurov.paceopp.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupFormDTO {
    @Size(min = 2,max = 30,message = "Имя должно иметь от 2 до 30 символов")
    private String username;

    @NotEmpty(message = "Почту нужно ввести обязательно")
    @Email(message = "Неверный формат почты")
    private String email;

    private String password;
    private String passwordConfirm;
}
