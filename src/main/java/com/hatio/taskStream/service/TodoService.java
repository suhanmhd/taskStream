package com.hatio.taskStream.service;

import com.hatio.taskStream.dto.TodoRequestDTO;
import com.hatio.taskStream.dto.TodoResponseDTO;

import java.util.List;
import java.util.UUID;

public interface TodoService {
    TodoResponseDTO createTodo(TodoRequestDTO todoRequestDTO);

    List<TodoResponseDTO> getTodosByProject(UUID projectId, String username);

    TodoResponseDTO updateTodo(UUID todoId, UUID id, TodoRequestDTO todoRequestDTO, String username);

    void deleteTodoFromProject(UUID todoId, UUID projectId, String username);
}
