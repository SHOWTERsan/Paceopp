package ru.santurov.paceopp.serives;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.santurov.paceopp.models.TokenType;
import ru.santurov.paceopp.models.User;
import ru.santurov.paceopp.models.VerificationToken;
import ru.santurov.paceopp.repositories.UserRepository;

import java.util.NoSuchElementException;


@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final UserRepository repo;
    private final TokenService token;

    @Autowired
    public EmailService(JavaMailSender mailSender, UserRepository repo, TokenService token) {
        this.mailSender = mailSender;
        this.repo = repo;
        this.token = token;
    }

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

    public String sendValidateMessage(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("transferpaceopp@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("Подтверждение почты");
        String vtoken = token.generateToken(user, TokenType.EMAIL_VERIFICATION);
        message.setText("Подтвердите почту перейдя по ссылке: http://localhost:8080/auth/confirm?token=" + vtoken +
                "\nСсылка будет активна в течении 30 минут.");
        try {
            mailSender.send(message);
            return vtoken;
        } catch (MailException e){
            throw new MailSendException("Ошибка отправки письма!");
        }
    }

    public void sendResetPasswordMessage(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("transferpaceopp@gmail.com");
        message.setTo(user.getEmail());
        message.setSubject("Сброс пароля");
        String vtoken = token.generateToken(user, TokenType.PASSWORD_RESET);
        message.setText("Сбросьте пароль перейдя по ссылке: http://localhost:8080/auth/reset_password?token=" + vtoken +
                "\nЕсли вы не запрашивали сброс пароля, то просто проигнорируйте это письмо.");
        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new MailSendException("Ошибка отправки письма!");
        }
    }
}
