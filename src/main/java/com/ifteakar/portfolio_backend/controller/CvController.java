package com.ifteakar.portfolio_backend.controller;

import com.ifteakar.portfolio_backend.model.CvDocument;
import com.ifteakar.portfolio_backend.repository.CvRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cv")
@CrossOrigin(origins = "*")
public class CvController {

    @Autowired
    private CvRepository cvRepository;

    @Value("${admin.token}")
    private String mySecretToken;

    @GetMapping
    public ResponseEntity<?> getCvStatus() {
        return cvRepository.findFirstByOrderByUploadDateDesc()
                .map(cv -> {
                    String formattedDate = cv.getUploadDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    return ResponseEntity.ok().body(Map.of(
                            "filename", cv.getFilename(),
                            "uploadDate", formattedDate,
                            "downloadUrl", "https://ifteakar-portfolio-backend.onrender.com/api/v1/cv/download"
                    ));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCv(
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @RequestParam("file") MultipartFile file) {

        if (adminToken == null || !adminToken.equals(mySecretToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Unauthorized!"));
        }

        try {
            cvRepository.deleteAll(); // পুরানো সিভি ডিলিট করে নতুনটা প্রাইমারি রাখবে
            CvDocument cv = new CvDocument();
            cv.setFilename(file.getOriginalFilename());
            cv.setFileData(file.getBytes());
            CvDocument savedCv = cvRepository.save(cv);

            String formattedDate = savedCv.getUploadDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "filename", savedCv.getFilename(),
                    "uploadDate", formattedDate,
                    "downloadUrl", "https://ifteakar-portfolio-backend.onrender.com/api/v1/cv/download"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadCv() {
        return cvRepository.findFirstByOrderByUploadDateDesc()
                .map(cv -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + cv.getFilename() + "\"")
                        .body(cv.getFileData()))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCv(@RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {
        if (adminToken == null || !adminToken.equals(mySecretToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Unauthorized!"));
        }
        cvRepository.deleteAll();
        return ResponseEntity.ok().build();
    }
}