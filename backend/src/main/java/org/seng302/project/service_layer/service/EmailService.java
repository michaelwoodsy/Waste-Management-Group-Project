package org.seng302.project.service_layer.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

/**
 * Service class for sending emails.
 */
@Service
@Data
@EnableAsync
public class EmailService {

    private JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email.
     * @param to Email address to send the email to.
     * @param subject Subject line of the email.
     * @param text Text content of the email.
     */
    @Async
    public void sendEmail(String to, String subject, String text) {
        var message = new SimpleMailMessage();
        message.setFrom("s302resale@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}