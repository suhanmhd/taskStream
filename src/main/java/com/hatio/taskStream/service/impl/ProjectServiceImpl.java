package com.hatio.taskStream.service.impl;

import com.hatio.taskStream.auth.entities.User;
import com.hatio.taskStream.auth.repositories.UserRepository;
import com.hatio.taskStream.auth.services.JwtService;
import com.hatio.taskStream.dto.ProjectRequestDTO;
import com.hatio.taskStream.dto.ProjectResponseDTO;
import com.hatio.taskStream.dto.TodoResponseDTO;
import com.hatio.taskStream.exception.ResourceCreationException;
import com.hatio.taskStream.exception.ResourceNotFoundException;
import com.hatio.taskStream.exception.UnauthorizedAccessException;
import com.hatio.taskStream.exception.UserNotFoundException;
import com.hatio.taskStream.model.Project;

import com.hatio.taskStream.repository.ProjectRepository;
import com.hatio.taskStream.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final JwtService jwtService;
    private  final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    private  final ModelMapper modelMapper;



    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    @Override
    public ProjectResponseDTO createProject(ProjectRequestDTO projectRequestDTO, String authHeader) {

        logger.info("Creating project for request: {}", projectRequestDTO);

        String token = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(token);
        logger.debug("Extracted user email: {}", userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {}", userEmail);
                    return new ResourceNotFoundException("User not found for email: " + userEmail);
                });

        Project project = Project.builder()
                .title(projectRequestDTO.getTitle())
                .createdDate(LocalDateTime.now())
                .todos(new ArrayList<>())
                .user(user)
                .build();

        try {
            Project savedProject = projectRepository.save(project);
            logger.info("Project saved successfully with ID: {}", savedProject.getId());
            return mapToProjectResponseDTO(savedProject);
        } catch (Exception e) {
            logger.error("Error saving project for user: {}", userEmail, e);
            throw new ResourceCreationException("Failed to save project");
        }
    }

    @Override
    public List<ProjectResponseDTO> getAllProjectsByUser(String username) {
        logger.info("Fetching user with username: {}", username);

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        logger.info("User found: {}", user.getUsername());

        List<Project> projects = projectRepository.findByUser(user);
        logger.info("Found {} todos for project {}", projects.size(), user.getUsername());

        return projects.stream()
                .map(this::mapToProjectResponseDTO)
                .toList();

    }

    @Override
    public ProjectResponseDTO getProjectById(UUID id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Project not found with ID: {}", id);
                    return new ResourceNotFoundException("Project not found with ID: " + id);
                });
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!project.getUser().getUsername().equals(username)) {
            logger.error("User: {} is not authorized to access project with ID: {}", username, id);
            throw new UnauthorizedAccessException("You are not authorized to access this project.");
        }

        logger.info("Project found: {}", project.getTitle());
        return mapToProjectResponseDTO(project);
    }

    @Override
    public ProjectResponseDTO updateProjectById(UUID id, ProjectRequestDTO projectRequestDTO, String username) {
        logger.info("Updating project with ID: {} for user: {}", id, username);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Project not found with ID: {}", id);
                    return new ResourceNotFoundException("Project not found with ID: " + id);
                });

        if (!project.getUser().getUsername().equals(username)) {
            logger.error("User: {} is not authorized to update project with ID: {}", username, id);
            throw new UnauthorizedAccessException("You are not authorized to update this project.");
        }
        project.setTitle(projectRequestDTO.getTitle());

        Project updatedProject = projectRepository.save(project);
        logger.info("Project updated successfully with ID: {}", id);

        return mapToProjectResponseDTO(updatedProject);
    }

    @Override
    public void deleteProjectById(UUID id, String username) {
        logger.info("Deleting project with ID: {} for user: {}", id, username);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Project not found with ID: {}", id);
                    return new ResourceNotFoundException("Project not found with ID: " + id);
                });

        if (!project.getUser().getUsername().equals(username)) {
            logger.error("User: {} is not authorized to delete project with ID: {}", username, id);
            throw new UnauthorizedAccessException("You are not authorized to delete this project.");
        }

        projectRepository.delete(project);
        logger.info("Project deleted successfully with ID: {}", id);
    }


    private ProjectResponseDTO mapToProjectResponseDTO(Project project) {
        logger.debug("Mapping Project entity to ProjectResponseDTO for project ID: {}", project.getId());
        return modelMapper.map(project, ProjectResponseDTO.class);
    }



}
