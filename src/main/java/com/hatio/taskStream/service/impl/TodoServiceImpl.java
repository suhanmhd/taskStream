package com.hatio.taskStream.service.impl;


import com.hatio.taskStream.dto.TodoRequestDTO;
import com.hatio.taskStream.dto.TodoResponseDTO;
import com.hatio.taskStream.exception.ResourceCreationException;
import com.hatio.taskStream.exception.ResourceNotFoundException;
import com.hatio.taskStream.exception.UnauthorizedAccessException;
import com.hatio.taskStream.model.Project;
import com.hatio.taskStream.model.Todo;
import com.hatio.taskStream.repository.ProjectRepository;
import com.hatio.taskStream.repository.TodoRepository;
import com.hatio.taskStream.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {
    private  final TodoRepository todoRepository;
    private  final ProjectRepository projectRepository;
    private  final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);
    @Override
    public TodoResponseDTO addTodo(TodoRequestDTO todoRequestDTO) {
        logger.info("Fetching Project with projectId: {}", todoRequestDTO.getProjectId());
        Optional<Project> optionalProject = projectRepository.findById(todoRequestDTO.getProjectId());

        if (optionalProject.isEmpty()) {

            logger.error("Project not found with ID: {}", todoRequestDTO.getProjectId());
            throw new ResourceNotFoundException("Project not found with id"+todoRequestDTO.getProjectId());
        }

        Project project = optionalProject.get();
        Todo todo = Todo.builder()
                .description(todoRequestDTO.getDescription())
                .status(todoRequestDTO.getStatus())
                .project(project)
                .build();

        try {
            Todo savedTodo = todoRepository.save(todo);
            logger.info("Todo created successfully with ID: {}", savedTodo.getId());
            return mapToTodoResponseDTO(savedTodo);
        } catch (Exception e) {
            logger.error("Error occurred while saving Todo: ",e);
            throw new ResourceCreationException("Failed to save the Todo.");
        }
    }

    @Override
    public List<TodoResponseDTO> getTodosByProject(UUID projectId, String username) {
        logger.info("Fetching Project with projectId: {}", projectId);
        Optional<Project> optionalProject = projectRepository.findById(projectId);

        if (optionalProject.isEmpty()) {
            logger.error("Project not found with ID: {}", projectId);
            throw new ResourceNotFoundException("Project not found with id"+projectId);
        }
        Project project = optionalProject.get();
        if (!project.getUser().getUsername().equals(username)) {
            logger.error("User: {} is not authorized to update project with ID: {}", username, projectId);
            throw new UnauthorizedAccessException("You are not authorized to update this project.");
        }


        List<Todo> todos = todoRepository.findByProject(project);
        logger.info("Retrieved {} todos for project ID: {}", todos.size(), projectId);
        return   todos.stream()
                .map(this::mapToTodoResponseDTO)
                .toList();
    }

    @Override
    public TodoResponseDTO updateTodo(UUID todoId, UUID id, TodoRequestDTO todoRequestDTO, String username) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> {
                    logger.error("Todo not found with ID:{}", todoId);
                    return new ResourceNotFoundException("Todo not found with ID: " + todoId);
                });
        Project project = todo.getProject();
        if (!project.getUser().getUsername().equals(username)) {
            String errorMsg = "User: " + username + " is not authorized to update this Todo with ID: " + todoId;
            logger.error(errorMsg);
            throw new UnauthorizedAccessException(errorMsg);
        }
        todo.setDescription(todoRequestDTO.getDescription());
        todo.setStatus(todoRequestDTO.getStatus());
        Todo updatedTodo = todoRepository.save(todo);
        logger.info("Todo with ID: {} updated successfully", todoId);
        return mapToTodoResponseDTO(updatedTodo);
    }

    @Override
    public void deleteTodoFromProject(UUID todoId, UUID projectId, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    String errorMsg = "Project not found with ID: " + projectId;
                    logger.error(errorMsg);
                    return new ResourceNotFoundException(errorMsg);
                });

        if (!project.getUser().getUsername().equals(username)) {
            String errorMsg = "User: " + username + " is not authorized to delete Todo from project with ID: " + projectId;
            logger.error(errorMsg);
            throw new UnauthorizedAccessException(errorMsg);
        }

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> {
                    String errorMsg = "Todo not found with ID: " + todoId;
                    logger.error(errorMsg);
                    return new ResourceNotFoundException(errorMsg);
                });

        if (!todo.getProject().getId().equals(projectId)) {
            String errorMsg = "Todo with ID: " + todoId + " does not belong to project with ID: " + projectId;
            logger.error(errorMsg);
            throw new ResourceNotFoundException(errorMsg);
        }

        todoRepository.delete(todo);

        logger.info("Todo with ID: {} deleted from project ID: {}", todoId, projectId);
    }



    private TodoResponseDTO mapToTodoResponseDTO(Todo todo) {
        logger.debug("Mapping Project entity to ProjectResponseDTO for project ID: {}", todo.getId());
        return modelMapper.map(todo, TodoResponseDTO.class);
    }

}
