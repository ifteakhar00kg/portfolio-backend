package com.ifteakar.portfolio_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "cv_documents")
@Data
public class CvDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    @Lob
    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    private byte[] fileData;

    private LocalDateTime uploadDate;

    @PrePersist
    protected void onCreate() {
        this.uploadDate = LocalDateTime.now();
    }
}