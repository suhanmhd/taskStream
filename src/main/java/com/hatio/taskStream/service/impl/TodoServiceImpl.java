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
import jakarta.transaction.Transactional;
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

    private final TodoRepository todoRepository;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);

    @Override
    @Transactional
    public TodoResponseDTO createTodo(TodoRequestDTO todoRequestDTO) {
        logger.info("Fetching Project with projectId: {}", todoRequestDTO.getProjectId());

        Project project = projectRepository.findById(todoRequestDTO.getProjectId())
                .orElseThrow(() -> {
                    logger.error("Project not found with ID: {}", todoRequestDTO.getProjectId());
                    return new ResourceNotFoundException("Project not found with ID: "+ todoRequestDTO.getProjectId());
                });

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
            logger.error("Error occurred while saving Todo: ", e);
            throw new ResourceCreationException("Failed to save the Todo.");
        }
    }

    @Override
    public List<TodoResponseDTO> getTodosByProject(UUID projectId, String username) {
        logger.info("Fetching Project with projectId: {}", projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    logger.error("Project not found with ID: {}", projectId);
                    return new ResourceNotFoundException(String.format("Project not found with ID: %s", projectId));
                });

        checkUserAuthorization(project, username);

        List<Todo> todos = todoRepository.findByProject(project);
        logger.info("Retrieved {} todos for project ID: {}", todos.size(), projectId);

        return todos.stream()
                .map(this::mapToTodoResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public TodoResponseDTO updateTodo(UUID projectId, UUID todoId, TodoRequestDTO todoRequestDTO, String username) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> {
                    logger.error("Todo not found with ID: {}", todoId);
                    return new ResourceNotFoundException(String.format("Todo not found with ID: %s", todoId));
                });

        validateTodoBelongsToProject(todo, projectId);
        checkUserAuthorization(todo.getProject(), username);

        todo.setDescription(todoRequestDTO.getDescription());
        todo.setStatus(todoRequestDTO.getStatus());

        Todo updatedTodo = todoRepository.save(todo);
        logger.info("Todo with ID: {} updated successfully", todoId);

        return mapToTodoResponseDTO(updatedTodo);
    }

    @Override
    @Transactional
    public void deleteTodoFromProject(UUID todoId, UUID projectId, String username) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    logger.error("Project not found with ID: {}", projectId);
                    return new ResourceNotFoundException(String.format("Project not found with ID: %s", projectId));
                });

        checkUserAuthorization(project, username);

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> {
                    logger.error("Todo not found with ID: {}", todoId);
                    return new ResourceNotFoundException(String.format("Todo not found with ID: %s", todoId));
                });

        validateTodoBelongsToProject(todo, projectId);

        todoRepository.delete(todo);
        logger.info("Todo with ID: {} deleted from project ID: {}", todoId, projectId);
    }

    private void validateTodoBelongsToProject(Todo todo, UUID projectId) {
        if (!todo.getProject().getId().equals(projectId)) {
            String errorMsg = String.format("Todo with ID: %s does not belong to project with ID: %s",
                    todo.getId(), projectId);
            logger.error(errorMsg);
            throw new ResourceNotFoundException(errorMsg);
        }
    }

    private void checkUserAuthorization(Project project, String username) {
        if (!project.getUser().getUsername().equals(username)) {
            String errorMsg = String.format("User: %s is not authorized to access this project with ID: %s",
                    username, project.getId());
            logger.error(errorMsg);
            throw new UnauthorizedAccessException(errorMsg);
        }
    }

    private TodoResponseDTO mapToTodoResponseDTO(Todo todo) {
        logger.debug("Mapping Todo entity to TodoResponseDTO for todo ID: {}", todo.getId());
        return modelMapper.map(todo, TodoResponseDTO.class);
    }
}
