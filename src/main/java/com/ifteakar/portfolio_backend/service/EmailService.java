package com.ifteakar.portfolio_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendAdminNotification(String visitorName, String visitorEmail, String messageContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("ifteakarahmed.kg@gmail.com");
        message.setSubject("New Portfolio Message from " + visitorName);
        message.setText("You received a new message!\n\n" +
                "Name: " + visitorName + "\n" +
                "Email: " + visitorEmail + "\n" +
                "Message:\n" + messageContent);

        mailSender.send(message);
    }

    public void sendAutoReplyToVisitor(String visitorEmail, String visitorName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(visitorEmail);
        message.setSubject("Thanks for reaching out! - Khandokar Ifteakar Ahmed");

        String emailBody = "Hi " + visitorName + ",\n\n" +
                "Thank you for reaching out through my portfolio website! " +
                "I have successfully received your message.\n\n" +
                "I will review your message and get back to you as soon as possible.\n\n" +
                "Best regards,\n" +
                "Khandokar Ifteakar Ahmed";

        message.setText(emailBody);
        mailSender.send(message);
    }
}