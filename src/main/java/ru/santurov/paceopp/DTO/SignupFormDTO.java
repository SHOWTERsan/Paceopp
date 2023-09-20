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

    @Size(min = 6,max = 255,message = "Пароль должен содержать от 8 символов до 255 символов.")
    private String password; //TODO Тут пизда в версте при удалении ошибки
    private String passwordConfirm;
}
