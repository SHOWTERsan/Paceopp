package ru.santurov.paceopp.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EmailMessageDTO {
    @NotEmpty(message = "Поле не должно быть пустым")
    private String subject;
    @NotEmpty(message = "Поле не должно быть пустым")
    private String message;
}
