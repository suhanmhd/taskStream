package com.hatio.taskStream.controller;

import com.hatio.taskStream.dto.ProjectRequestDTO;
import com.hatio.taskStream.dto.ProjectResponseDTO;
import com.hatio.taskStream.dto.TokenDTO;
import com.hatio.taskStream.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;
    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);


    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(@RequestBody ProjectRequestDTO projectRequestDTO,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        ProjectResponseDTO response = projectService.createProject(projectRequestDTO,userDetails.getUsername());
        logger.info("Project created with ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public List<ProjectResponseDTO> getAllProjectsByUser(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        logger.info("Fetching all projects for user: {}", username);
        return projectService.getAllProjectsByUser(username);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable UUID id) {
        logger.info("Received request to fetch project with ID: {}", id);
        ProjectResponseDTO projectResponseDTO = projectService.getProjectById(id);
        return ResponseEntity.ok(projectResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProjectById(@PathVariable UUID id,
                                                                @RequestBody ProjectRequestDTO projectRequestDTO,
                                                                @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("User: {} requested update for project with ID: {}", userDetails.getUsername(), id);
        ProjectResponseDTO updatedProject = projectService.updateProjectById(id, projectRequestDTO, userDetails.getUsername());
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectById(@PathVariable UUID id,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("User: {} requested deletion of project with ID: {}", userDetails.getUsername(), id);
        projectService.deleteProjectById(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/github/tokens")
    public ResponseEntity<String> saveGitHubToken(@RequestBody TokenDTO token) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Saving GitHub token for user: {}", username);
        projectService.saveGitHubToken(username, token.getToken());
        return ResponseEntity.ok("GitHub token saved successfully.");
    }

    @PostMapping("/{projectId}/export")
    public ResponseEntity<String> exportProjectSummary(@PathVariable UUID projectId) {
        logger.info("Exporting project summary as gist for project ID: {}", projectId);

        String result = projectService.exportProjectSummaryAsGist(projectId);

        if (result.startsWith("REDIRECT:")) {
            return ResponseEntity.status(HttpStatus.FOUND).header("Location", result.substring(9)).build();
        }

        return ResponseEntity.ok(result);

    }
}
