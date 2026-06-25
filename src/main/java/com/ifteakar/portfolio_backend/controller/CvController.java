package com.ifteakar.portfolio_backend.controller;

import com.ifteakar.portfolio_backend.service.SupabaseStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/cv")
@CrossOrigin(origins = "*")
public class CvController {

    private final SupabaseStorageService storageService;

    public CvController(SupabaseStorageService storageService) {
        this.storageService = storageService;
    }

    @Value("${admin.token}")
    private String mySecretToken;

    @GetMapping
    public ResponseEntity<?> getCvStatus() {
        return ResponseEntity.ok(
                Map.of(
                        "downloadUrl",
                        storageService.getCvUrl()
                )
        );
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadCv(
            @RequestHeader(value = "X-Admin-Token", required = false)
            String adminToken,

            @RequestParam("file")
            MultipartFile file) {

        if (adminToken == null ||
                !adminToken.equals(mySecretToken)) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "success",
                                    false
                            )
                    );
        }

        try {

            String url = storageService.uploadCv(file);

            return ResponseEntity.ok(
                    Map.of(
                            "success",
                            true,
                            "downloadUrl",
                            url
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "success",
                                    false,
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadCv() {

        return ResponseEntity.status(302)
                .header(
                        "Location",
                        storageService.getCvUrl()
                )
                .build();
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}