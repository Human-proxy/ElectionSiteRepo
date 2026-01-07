package nl.hva.dederdekamer.election_backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Service for sending emails using Spring Mail and Mailtrap
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.email.sender}")
    private String emailSender;

    /**
     * Load email template from resources
     */
    private String loadTemplate(String templateName) throws IOException {
        ClassPathResource resource = new ClassPathResource("email-templates/" + templateName);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    /**
     * Send a verification email with a 4-digit code
     * @param to Recipient email address
     * @param username User's username
     * @param verificationCode 4-digit verification code
     * @throws MessagingException if email sending fails
     */
    public void sendVerificationEmail(String to, String username, String verificationCode) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(emailSender);
        helper.setTo(to);
        helper.setSubject("Verifieer je emailadres - StemWijs");

        try {
            String template = loadTemplate("verification-email.html");
            String htmlContent = template
                .replace("{{username}}", username)
                .replace("{{code}}", verificationCode);
            helper.setText(htmlContent, true);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template", e);
        }

        mailSender.send(message);
    }

    /**
     * Send a password reset email with a secure link
     * @param to Recipient email address
     * @param username User's username
     * @param token Password reset token
     * @throws MessagingException if email sending fails
     */
    public void sendPasswordResetEmail(String to, String username, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(emailSender);
        helper.setTo(to);
        helper.setSubject("Wachtwoord resetten - StemWijs");

        try {
            String resetLink = frontendUrl + "/wachtwoord-resetten?token=" + token;
            String template = loadTemplate("password-reset-email.html");
            String htmlContent = template
                .replace("{{username}}", username)
                .replace("{{resetLink}}", resetLink);
            helper.setText(htmlContent, true);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template", e);
        }

        mailSender.send(message);
    }

    /**
     * Send an account deactivation confirmation email
     * @param to Recipient email address
     * @param username User's username
     * @throws MessagingException if email sending fails
     */
    public void sendAccountDeactivationEmail(String to, String username) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("hello@keepitme.com");
        helper.setTo(to);
        helper.setSubject("Account Gedeactiveerd - StemWijs");

        try {
            String loginLink = frontendUrl + "/inloggen";
            String template = loadTemplate("account-deactivation-email.html");
            String htmlContent = template
                .replace("{{username}}", username)
                .replace("{{loginLink}}", loginLink);
            helper.setText(htmlContent, true);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template", e);
        }

        mailSender.send(message);
    }
}
