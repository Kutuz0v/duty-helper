package scpc.dutyhelper.auth.service;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);

    void sendSignUpConfirmationEmail(String to, String baseUrl, String confirmationCode);

    void sendResetPasswordConfirmation(String email, String baseUrl, String confirmationCode);
}
