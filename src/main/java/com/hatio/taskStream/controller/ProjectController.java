package com.hatio.taskStream.controller;

import com.hatio.taskStream.dto.ProjectRequestDTO;
import com.hatio.taskStream.dto.ProjectResponseDTO;
import com.hatio.taskStream.service.ProjectService;
import com.sun.security.auth.UserPrincipal;
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

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
public class ProjectController {

    private  final ProjectService projectService;

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @PostMapping("/create")
    public  ResponseEntity<ProjectResponseDTO>createProject(@RequestBody ProjectRequestDTO projectRequestDTO, @RequestHeader( HttpHeaders.AUTHORIZATION )String authHeader) {
        ProjectResponseDTO response = projectService.createProject(projectRequestDTO,authHeader);
         return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping("/all")
    public List<ProjectResponseDTO> getAllProjectsByUser(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
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
        logger.info("Received request from user: {} to update project with ID: {}", userDetails.getUsername(), id);

        ProjectResponseDTO updatedProject = projectService.updateProjectById(id,projectRequestDTO,userDetails.getUsername());
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProjectById(@PathVariable UUID id,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Received request from user: {} to delete project with ID: {}", userDetails.getUsername(), id);

        projectService.deleteProjectById(id, userDetails.getUsername());
        return ResponseEntity.ok("Project deleted successfully");
    }

}
