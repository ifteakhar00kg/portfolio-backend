package com.ifteakar.portfolio_backend.controller;

import com.ifteakar.portfolio_backend.model.Project;
import com.ifteakar.portfolio_backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/projects")
@CrossOrigin(origins = "http://localhost:8080")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return ResponseEntity.ok(projects);
    }

    @PostMapping
    public ResponseEntity<?> addProject(
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @RequestBody Project project) {

        // আপনার নিজের একটি সিক্রেট পাসওয়ার্ড বা টোকেন এখানে সেট করুন
        String mySecretToken = "ifteakar_super_secret_token_2026";

        if (adminToken == null || !adminToken.equals(mySecretToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Unauthorized! You are not allowed to add projects."
            ));
        }

        try {
            Project savedProject = projectRepository.save(project);
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Project added successfully!",
                    "data", savedProject
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to add project: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @PathVariable Long id) {

        String mySecretToken = "ifteakar_super_secret_token_2026";

        if (adminToken == null || !adminToken.equals(mySecretToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Unauthorized!"
            ));
        }

        try {
            if (!projectRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "success", false,
                        "message", "Project not found!"
                ));
            }
            projectRepository.deleteById(id);
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Project deleted successfully!"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to delete project: " + e.getMessage()
            ));
        }
    }
}