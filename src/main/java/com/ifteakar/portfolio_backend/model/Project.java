package com.ifteakar.portfolio_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@Data
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @JsonProperty("techStack")
    private String technologies;

    @JsonProperty("githubUrl")
    private String githubLink;

    @JsonProperty("liveUrl")
    private String liveLink;

    @JsonProperty("imageUrl")
    private String imageLink;

    private String category;
    private String year;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}