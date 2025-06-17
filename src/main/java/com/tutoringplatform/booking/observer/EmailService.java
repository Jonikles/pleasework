package com.tutoringplatform.booking.observer;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${app.email.from:noreply@tutoringplatform.com}")
    private String fromAddress;

    @Value("${app.email.mock:true}")
    private boolean mockMode;

    public void sendEmail(String to, String subject, String body) {
        if (!emailEnabled) {
            logger.debug("Email sending is disabled. Skipping email to: {}", to);
            return;
        }

        if (mockMode) {
            // In mock mode, just log the email instead of sending
            logEmail(to, subject, body);
        } else {
            // In production, this would integrate with an email service
            // like SendGrid, AWS SES, or SMTP
            sendActualEmail(to, subject, body);
        }
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        if (!emailEnabled) {
            logger.debug("Email sending is disabled. Skipping email to: {}", to);
            return;
        }

        if (mockMode) {
            logEmail(to, subject, htmlBody);
        } else {
            sendActualHtmlEmail(to, subject, htmlBody);
        }
    }

    private void logEmail(String to, String subject, String body) {
        logger.info("=== MOCK EMAIL ===");
        logger.info("From: {}", fromAddress);
        logger.info("To: {}", to);
        logger.info("Subject: {}", subject);
        logger.info("Body:\n{}", body);
        logger.info("==================");
    }

    private void sendActualEmail(String to, String subject, String body) {
        // TODO: Implement actual email sending
        // This would integrate with a real email service
        // For now, just log that we would send it
        logger.info("Would send email to {} with subject: {}", to, subject);

        // Example implementation with JavaMail (commented out):
        /*
         * try {
         * MimeMessage message = mailSender.createMimeMessage();
         * MimeMessageHelper helper = new MimeMessageHelper(message, true);
         * 
         * helper.setFrom(fromAddress);
         * helper.setTo(to);
         * helper.setSubject(subject);
         * helper.setText(body, false);
         * 
         * mailSender.send(message);
         * logger.info("Email sent successfully to: {}", to);
         * } catch (Exception e) {
         * logger.error("Failed to send email to: {}", to, e);
         * throw new EmailSendException("Failed to send email", e);
         * }
         */
    }

    private void sendActualHtmlEmail(String to, String subject, String htmlBody) {
        // TODO: Implement actual HTML email sending
        logger.info("Would send HTML email to {} with subject: {}", to, subject);
    }

    // Utility method to send emails with retry logic
    public void sendEmailWithRetry(String to, String subject, String body, int maxRetries) {
        int attempts = 0;
        Exception lastException = null;

        while (attempts < maxRetries) {
            try {
                sendEmail(to, subject, body);
                return; // Success
            } catch (Exception e) {
                lastException = e;
                attempts++;
                logger.warn("Email send attempt {} failed for {}", attempts, to, e);

                if (attempts < maxRetries) {
                    try {
                        // Exponential backoff
                        Thread.sleep((long) Math.pow(2, attempts) * 1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }

        logger.error("Failed to send email after {} attempts to: {}", maxRetries, to, lastException);
    }

    // Custom exception for email sending failures
    public static class EmailSendException extends RuntimeException {
        public EmailSendException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}