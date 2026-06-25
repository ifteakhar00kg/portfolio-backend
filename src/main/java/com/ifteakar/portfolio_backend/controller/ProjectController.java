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
@CrossOrigin(origins = "*")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    private final String mySecretToken = "ifteakar_super_secret_token_2026";

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return ResponseEntity.ok(projects);
    }

    @PostMapping
    public ResponseEntity<?> addProject(
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @RequestBody Project project) {

        if (adminToken == null || !adminToken.equals(mySecretToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Unauthorized! You are not allowed to add projects."
            ));
        }
        try {
            Project savedProject = projectRepository.save(project);
            return ResponseEntity.ok().body(savedProject);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to add project: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @PathVariable Long id,
            @RequestBody Project projectDetails) {

        if (adminToken == null || !adminToken.equals(mySecretToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false, "message", "Unauthorized!"
            ));
        }

        java.util.Optional<Project> projectOptional = projectRepository.findById(id);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            project.setTitle(projectDetails.getTitle());
            project.setDescription(projectDetails.getDescription());
            project.setTechnologies(projectDetails.getTechnologies());
            project.setGithubLink(projectDetails.getGithubLink());
            project.setLiveLink(projectDetails.getLiveLink());
            project.setImageLink(projectDetails.getImageLink());
            project.setCategory(projectDetails.getCategory());
            project.setYear(projectDetails.getYear());

            Project updatedProject = projectRepository.save(project);
            return ResponseEntity.ok().body(updatedProject);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false, "message", "Project not found!"
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @PathVariable Long id) {

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