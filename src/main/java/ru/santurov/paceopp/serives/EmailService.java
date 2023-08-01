package ru.santurov.paceopp.serives;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.santurov.paceopp.repositories.UserRepository;


@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final UserRepository repo;

    @Autowired
    public EmailService(JavaMailSender mailSender, UserRepository repo) {
        this.mailSender = mailSender;
        this.repo = repo;
    }

    //TODO Как то обработать не вошедшего пользователя
    public void sendMessage(String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("transferpaceopp@gmail.com");
        message.setTo("santurovs866@gmail.com");
        message.setSubject(subject);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String email = repo.findByUsername(username).get().getEmail();
        message.setText("Пользователь: " + username + " с почтой: " + email + "\nОтправил сообщение: " + text);
        try {
            mailSender.send(message);
        } catch (MailException e){
            throw new MailSendException("Ошибка отправки письма!");
        }
    }

    public void sendValidateMessage(String email, String uuid) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("transferpaceopp@gmail.com");
        message.setTo(email);
        message.setSubject("Подтверждение почты");
        message.setText("Подтвердите почту перейдя по ссылке: http://localhost:8080/auth/confirm?uuid=" + uuid);
        try {
            mailSender.send(message);
        } catch (MailException e){
            throw new MailSendException("Ошибка отправки письма!");
        }
    }
}
