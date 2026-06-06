package com.ifteakar.portfolio_backend.controller;

import com.ifteakar.portfolio_backend.model.ContactMessage;
import com.ifteakar.portfolio_backend.repository.ContactRepository;
import com.ifteakar.portfolio_backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/contact")
@CrossOrigin(origins = "*")
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private EmailService emailService;

    // ক্রন-জবের সার্ভার জাগিয়ে রাখার জন্য নতুন GET এন্ডপয়েন্ট
    @GetMapping("/ping")
    public ResponseEntity<?> keepAlivePing() {
        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "status", "Server is up and running!"
        ));
    }

    @PostMapping
    public ResponseEntity<?> receiveMessage(@RequestBody ContactMessage message) {
        try {
            ContactMessage savedMessage = contactRepository.save(message);

            emailService.sendAdminNotification(savedMessage.getName(), savedMessage.getEmail(), savedMessage.getMessage());

            emailService.sendAutoReplyToVisitor(savedMessage.getEmail(), savedMessage.getName());

            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Thank you! Your message has been sent successfully."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to send message."
            ));
        }
    }
}