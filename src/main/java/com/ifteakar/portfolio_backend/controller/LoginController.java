package com.ifteakar.portfolio_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin(origins = "*")
public class LoginController {

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.token}")
    private String adminSecretToken;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String inputPassword = request.get("password");

        if (inputPassword != null && inputPassword.equals(adminPassword)) {
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "token", adminSecretToken,
                    "message", "Login successful!"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Invalid admin password!"
            ));
        }
    }
}