package com.hatio.taskStream.service.impl;

import org.junit.jupiter.api.Test;


import com.hatio.taskStream.auth.entities.User;
import com.hatio.taskStream.auth.repositories.UserRepository;
import com.hatio.taskStream.auth.services.JwtService;
import com.hatio.taskStream.dto.ProjectRequestDTO;
import com.hatio.taskStream.dto.ProjectResponseDTO;
import com.hatio.taskStream.exception.ResourceCreationException;
import com.hatio.taskStream.exception.ResourceNotFoundException;
import com.hatio.taskStream.exception.UnauthorizedAccessException;
import com.hatio.taskStream.model.Project;
import com.hatio.taskStream.repository.ProjectRepository;
import com.hatio.taskStream.service.GitHubAuthTokenService;
import com.hatio.taskStream.service.GitHubGistService;
import com.hatio.taskStream.service.MarkdownService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private User user;
    private Project project;
    private ProjectRequestDTO projectRequestDTO;
    private ProjectResponseDTO projectResponseDTO;
    private String token;
    private String authHeader;
    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {


        user = new User();
        user.setUserId(1);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        projectRequestDTO = new ProjectRequestDTO();
        projectRequestDTO.setTitle("Test Project");

        project = Project.builder()
                .id(UUID.randomUUID())
                .title("Test Project")
                .createdDate(LocalDateTime.now())
                .todos(new ArrayList<>())
                .user(user)
                .build();

        projectResponseDTO = new ProjectResponseDTO();
        projectResponseDTO.setId(project.getId());
        projectResponseDTO.setTitle(project.getTitle());
        Authentication authentication = Mockito.mock(Authentication.class);
//        Mockito.when(authentication.getName()).thenReturn("testUser");
//        SecurityContextHolder.getContext().setAuthentication(authentication);


        token = "jwt-token";
        authHeader = "Bearer " + token;
    }

    @Test
    void testCreateProject_Success() {
        // Mock the behavior of userRepository and projectRepository
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(modelMapper.map(any(Project.class), eq(ProjectResponseDTO.class))).thenReturn(projectResponseDTO);

        // Call the service method
        ProjectResponseDTO responseDTO = projectService.createProject(projectRequestDTO, "test@example.com");

        // Verify the results
        assertNotNull(responseDTO);
        assertEquals("Test Project", responseDTO.getTitle());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void testCreateProject_UserNotFound() {
        // Mock the behavior of userRepository to return an empty Optional
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Call the service method and assert that an exception is thrown
        ResourceNotFoundException thrown = assertThrows(ResourceNotFoundException.class, () -> {
            projectService.createProject(projectRequestDTO, "test@example.com");
        });

        assertEquals("User not found for email: test@example.com", thrown.getMessage());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void testCreateProject_Failure() {
        // Mock the behavior of userRepository and projectRepository
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.save(any(Project.class))).thenThrow(new RuntimeException("Database error"));

        // Call the service method and assert that an exception is thrown
        ResourceCreationException thrown = assertThrows(ResourceCreationException.class, () -> {
            projectService.createProject(projectRequestDTO, "test@example.com");
        });

        assertEquals("Failed to save project", thrown.getMessage());
        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(projectRepository, times(1)).save(any(Project.class));
    }
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        // Initialize your Project and ProjectResponseDTO objects here
//        project = new Project();
//        project.setId(1L);
//
//        projectResponseDTO = new ProjectResponseDTO();
//        projectResponseDTO.setId(1L);
//
//        // Mock SecurityContext
//        SecurityContextHolder.setContext(securityContext);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        when(authentication.getName()).thenReturn("testUser");
//    }

    @Test
    void getAllProjectsByUser_Success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(projectRepository.findByUser(user)).thenReturn(List.of(project));
        when(modelMapper.map(project, ProjectResponseDTO.class)).thenReturn(projectResponseDTO);

        List<ProjectResponseDTO> result = projectService.getAllProjectsByUser(user.getEmail());

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(projectRepository, times(1)).findByUser(user);
    }

    @Test
    void getAllProjectsByUser_UserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            projectService.getAllProjectsByUser(user.getEmail());
        });

        assertEquals("User not found with username: " + user.getEmail(), exception.getMessage());
    }

    @Test
    void getProjectById_Success() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        when(modelMapper.map(project, ProjectResponseDTO.class)).thenReturn(projectResponseDTO);

        ProjectResponseDTO result = projectService.getProjectById(project.getId());

        assertNotNull(result);
        assertEquals(projectResponseDTO.getId(), result.getId());
        verify(projectRepository, times(1)).findById(project.getId());
    }

    @Test
    void getProjectById_NotFound() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            projectService.getProjectById(project.getId());
        });

        assertEquals("Project not found with ID: " + project.getId(), exception.getMessage());
    }

//    @Test
//    void getProjectById_UnauthorizedAccess() {
//        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
//        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("anotherUser");
//
//        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () -> {
//            projectService.getProjectById(project.getId());
//        });
//
//        assertEquals("You are not authorized to access this project.", exception.getMessage());
//    }

    @Test
    void updateProjectById_Success() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(modelMapper.map(project, ProjectResponseDTO.class)).thenReturn(projectResponseDTO);

        ProjectResponseDTO result = projectService.updateProjectById(project.getId(), projectRequestDTO, user.getUsername());

        assertNotNull(result);
        assertEquals(projectResponseDTO.getId(), result.getId());
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void updateProjectById_NotFound() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            projectService.updateProjectById(project.getId(), projectRequestDTO, user.getUsername());
        });

        assertEquals("Project not found with ID: " + project.getId(), exception.getMessage());
    }

    @Test
    void updateProjectById_UnauthorizedAccess() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(project.getUser().getUsername()).thenReturn("anotherUser");

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () -> {
            projectService.updateProjectById(project.getId(), projectRequestDTO, user.getUsername());
        });

        assertEquals("You are not authorized to update this project.", exception.getMessage());
    }

    @Test
    void deleteProjectById_Success() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(project.getUser().getUsername()).thenReturn(user.getUsername());

        projectService.deleteProjectById(project.getId(), user.getUsername());

        verify(projectRepository, times(1)).delete(project);
    }

    @Test
    void deleteProjectById_NotFound() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            projectService.deleteProjectById(project.getId(), user.getUsername());
        });

        assertEquals("Project not found with ID: " + project.getId(), exception.getMessage());
    }

    @Test
    void deleteProjectById_UnauthorizedAccess() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(project.getUser().getUsername()).thenReturn("anotherUser");

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () -> {
            projectService.deleteProjectById(project.getId(), user.getUsername());
        });

        assertEquals("You are not authorized to delete this project.", exception.getMessage());
    }

    @Test
    void saveGitHubToken_Success() {
        when(userRepository.findByEmail(user.getUsername())).thenReturn(Optional.of(user));

        projectService.saveGitHubToken(user.getUsername(), token);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void saveGitHubToken_UserNotFound() {
        when(userRepository.findByEmail(user.getUsername())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            projectService.saveGitHubToken(user.getUsername(), token);
        });

        assertEquals("User not found with username: " + user.getUsername(), exception.getMessage());
    }

//    @Test
//    void exportProjectSummaryAsGist_Success() {
//        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
//        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(user.getUsername());
//        when(markdownService.generateMarkdownSummary(project)).thenReturn("Project Summary");
//        when(gitHubGistService.createSecretGist(anyString(), anyString(), anyString())).thenReturn("gistUrl");
//
//        String gistUrl = projectService.exportProjectSummaryAsGist(project.getId());
//
//        assertNotNull(gistUrl);
//        assertEquals("gistUrl", gistUrl);
//        verify(gitHubGistService, times(1)).createSecretGist(anyString(), anyString(), anyBoolean(), anyString());
//    }

    @Test
    void exportProjectSummaryAsGist_NotFound() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            projectService.exportProjectSummaryAsGist(project.getId());
        });

        assertEquals("Project not found with ID: " + project.getId(), exception.getMessage());
    }

    @Test
    void exportProjectSummaryAsGist_UnauthorizedAccess() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("anotherUser");

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class, () -> {
            projectService.exportProjectSummaryAsGist(project.getId());
        });

        assertEquals("You are not authorized to export this project's summary.", exception.getMessage());
    }
}
