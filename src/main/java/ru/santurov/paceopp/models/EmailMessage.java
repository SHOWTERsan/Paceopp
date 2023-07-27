package ru.santurov.paceopp.models;

import jakarta.validation.constraints.NotEmpty;

public class EmailMessage {
    @NotEmpty(message = "Поле не должно быть пустым")
    private String subject;
    @NotEmpty(message = "Поле не должно быть пустым")
    private String message;

    public EmailMessage() {
    }

    public EmailMessage(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }

    public String getSubject() { return subject; }

    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }
}
