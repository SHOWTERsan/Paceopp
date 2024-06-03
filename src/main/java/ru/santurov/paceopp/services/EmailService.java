package ru.santurov.paceopp.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.santurov.paceopp.models.Audio;
import ru.santurov.paceopp.models.TokenType;
import ru.santurov.paceopp.models.User;

import java.io.File;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final UserService userService;
    private final TokenService token;
    @Value("${mail.from.address}")
    private String fromAddress;

    public void sendMessage(String subject, String text, String toEmail, List<Audio> audioFiles) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(text);

            for (Audio audio : audioFiles) {
                ByteArrayResource byteArrayResource = new ByteArrayResource(audio.getData());
                String fileName = audio.getBeat().getName();
                String fileExtension = audio.getFileFormat();
                helper.addAttachment(fileName + "." + fileExtension, byteArrayResource);
            }
        };

        try {
            mailSender.send(messagePreparator);
        } catch (MailException e) {
            throw new MailSendException("Ошибка отправки письма!", e);
        }
    }
    public void sendMessage(String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo("santurovs866@gmail.com");
        message.setSubject(subject);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String email = userService.findByUsername(username).get().getEmail();
        message.setText("Пользователь: " + username + " с почтой: " + email + "\nОтправил сообщение: " + text);
        try {
            mailSender.send(message);
        } catch (MailException e){
            throw new MailSendException("Ошибка отправки письма!");
        }
    }


    @Async
    public void sendValidateMessage(User user, String baseUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        if (user.isVerified())
            message.setTo(user.getTempEmail());
        else
            message.setTo(user.getEmail());
        message.setSubject("Подтверждение почты");
        String vtoken = token.generateToken(user, TokenType.EMAIL_VERIFICATION);
        String confirmationUrl = baseUrl + "/auth/confirmEmail?token=" + vtoken;
        message.setText("Подтвердите почту перейдя по ссылке:" +  confirmationUrl +
                "\nСсылка будет активна в течении 30 минут.");
        try {
            mailSender.send(message);
        } catch (MailException e){
            throw new MailSendException("Ошибка отправки письма!");
        }
    }


    @Async
    public void sendForgotPasswordMessage(User user, String baseUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(user.getEmail());
        message.setSubject("Сброс пароля");
        String vtoken = token.generateToken(user, TokenType.PASSWORD_RESET);
        String confirmationUrl = baseUrl + "/auth/resetPassword?token=" + vtoken;
        message.setText("Сбросьте пароль перейдя по ссылке: " + confirmationUrl +
                "\nЕсли вы не запрашивали сброс пароля, то просто проигнорируйте это письмо.");
        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new MailSendException("Ошибка отправки письма!");
        }
    }
}
