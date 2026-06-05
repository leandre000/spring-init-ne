package com.utility.billing.service.impl;

import com.utility.billing.config.MailProperties;
import com.utility.billing.entity.Bill;
import com.utility.billing.entity.Customer;
import com.utility.billing.entity.Payment;
import com.utility.billing.entity.User;
import com.utility.billing.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Service implementation for asynchronous email notifications.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Override
    @Async("emailTaskExecutor")
    public void sendVerificationEmail(User user, String token) {
        log.info("Preparing registration verification email for user: {}", user.getEmail());
        String confirmUrl = mailProperties.getBaseUrl() + "/api/v1/auth/verify-email?token=" + token;

        Map<String, String> variables = Map.of(
                "appName", mailProperties.getFromName(),
                "firstName", user.getFullName(),
                "confirmUrl", confirmUrl
        );

        String htmlBody = loadTemplate("verification.html", variables);
        sendHtmlEmail(user.getEmail(), "Verify your email address", htmlBody);
    }

    @Override
    @Async("emailTaskExecutor")
    public void sendPasswordResetEmail(User user, String token) {
        log.info("Preparing password reset email for user: {}", user.getEmail());
        String resetUrl = mailProperties.getBaseUrl() + "/api/v1/auth/reset-password?token=" + token;

        Map<String, String> variables = Map.of(
                "appName", mailProperties.getFromName(),
                "firstName", user.getFullName(),
                "resetUrl", resetUrl
        );

        String htmlBody = loadTemplate("password-reset.html", variables);
        sendHtmlEmail(user.getEmail(), "Password Reset Request", htmlBody);
    }

    @Override
    @Async("emailTaskExecutor")
    public void sendBillNotificationEmail(Customer customer, Bill bill) {
        log.info("Preparing bill notification email for customer: {}", customer.getEmail());
        String message = "Your bill for " + bill.getBillingMonth() + "/" + bill.getBillingYear() +
                " is ready. Total amount due is " + bill.getTotalAmount() + " FRW. Please pay by the due date to avoid penalties.";

        Map<String, String> variables = Map.of(
                "appName", mailProperties.getFromName(),
                "customerName", customer.getFullName(),
                "message", message,
                "portalUrl", mailProperties.getBaseUrl()
        );

        String htmlBody = loadTemplate("notification.html", variables);
        sendHtmlEmail(customer.getEmail(), "New Utility Bill Issued", htmlBody);
    }

    @Override
    @Async("emailTaskExecutor")
    public void sendPaymentNotificationEmail(Customer customer, Payment payment) {
        log.info("Preparing payment receipt email for customer: {}", customer.getEmail());
        String message = "Thank you! We have received your payment of " + payment.getAmountPaid() +
                " FRW on " + payment.getPaymentDate() + ". Your payment reference is: " + payment.getPaymentReference();

        Map<String, String> variables = Map.of(
                "appName", mailProperties.getFromName(),
                "customerName", customer.getFullName(),
                "message", message,
                "portalUrl", mailProperties.getBaseUrl()
        );

        String htmlBody = loadTemplate("notification.html", variables);
        sendHtmlEmail(customer.getEmail(), "Payment Receipt Confirmation", htmlBody);
    }

    /**
     * Loads an HTML email template and replaces placeholders.
     */
    private String loadTemplate(String templateName, Map<String, String> variables) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email/" + templateName);
            byte[] bytes = resource.getInputStream().readAllBytes();
            String content = new String(bytes, StandardCharsets.UTF_8);
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                content = content.replace("{{" + entry.getKey() + "}}", entry.getValue() != null ? entry.getValue() : "");
            }
            return content;
        } catch (IOException e) {
            log.error("Failed to load email template: {}", templateName, e);
            throw new RuntimeException("Email template load failed", e);
        }
    }

    /**
     * Helper method to send html messages via JavaMailSender.
     */
    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailProperties.getFrom(), mailProperties.getFromName());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}", to, e);
        }
    }
}
