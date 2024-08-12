package com.hatio.taskStream.service;

import com.hatio.taskStream.dto.ProjectRequestDTO;
import com.hatio.taskStream.dto.ProjectResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    ProjectResponseDTO createProject(ProjectRequestDTO projectRequestDTO, String authHeader);

    List<ProjectResponseDTO> getAllProjectsByUser(String username);

    ProjectResponseDTO getProjectById(UUID id);

    ProjectResponseDTO updateProjectById(UUID id, ProjectRequestDTO projectRequestDTO, String username);

    void deleteProjectById(UUID id, String username);

    void saveGitHubToken(String username, String token);

    String exportProjectSummaryAsGist(UUID projectId);
}
