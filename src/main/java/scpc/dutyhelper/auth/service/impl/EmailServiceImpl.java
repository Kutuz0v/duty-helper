package scpc.dutyhelper.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import scpc.dutyhelper.auth.service.EmailService;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender emailSender;

    @Async
    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply.duty.helper@scpc.gov.ua");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
        log.info("Simple message {} sent to {}", text, to);
    }

    @Async
    @Override
    public void sendSignUpConfirmationEmail(String to, String baseUrl, String confirmationCode) {
        String subject = "Реєстрація в сервісі DutyHelper";
        String URL = baseUrl + "?confirmationCode=" + confirmationCode;
        String message = String.format("""
                Для підтвердження реєстрації перейдіть за посиланням:
                \r%s
                \r
                \rЯкщо ви отримали цей лист випадково, проігноруйте його.""", URL);

        sendSimpleMessage(to, subject, message);
    }

    @Async
    @Override
    public void sendResetPasswordConfirmation(String email, String baseUrl, String confirmationCode) {
        String subject = "Запит на скидання паролю в сервісі DutyHelper";
        String URL = baseUrl + "?confirmationCode=" + confirmationCode;
        String message = String.format("""
                Для скидання паролю перейдіть за посиланням:
                \r%s
                \r
                \rЯкщо ви отримали цей лист випадково, проігноруйте (видаліть) його.""", URL);

        sendSimpleMessage(email, subject, message);
    }
}
