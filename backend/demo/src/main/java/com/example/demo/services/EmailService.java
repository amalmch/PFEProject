package com.example.demo.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            // Create a MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Set email parameters
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            // You can set the sender’s email address here if needed
            helper.setFrom("no-reply@vermeg.com");

            // Send the email
            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace(); // Handle email sending failure
            throw new RuntimeException("Failed to send email", e);
        }
    }
}

