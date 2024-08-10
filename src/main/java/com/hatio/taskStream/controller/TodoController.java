package com.hatio.taskStream.controller;

import com.hatio.taskStream.dto.TodoRequestDTO;
import com.hatio.taskStream.dto.TodoResponseDTO;
import com.hatio.taskStream.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/projects/{projectId}/todos")
@RequiredArgsConstructor
public class TodoController {

    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);
   private  final TodoService todoService;
     @PostMapping("/create")
    public ResponseEntity<TodoResponseDTO> addTodo(@Valid @RequestBody TodoRequestDTO todoRequestDTO) {
        TodoResponseDTO todoResponseDTO = todoService.addTodo(todoRequestDTO);
        return ResponseEntity.ok(todoResponseDTO);
    }
    @GetMapping
    public ResponseEntity<List<TodoResponseDTO>>getTodosByProject(@PathVariable UUID projectId,@AuthenticationPrincipal UserDetails userDetails){
         List<TodoResponseDTO>todoList = todoService.getTodosByProject(projectId,userDetails.getUsername());
         return  ResponseEntity.ok(todoList);
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<TodoResponseDTO> updateTodo(
            @PathVariable UUID projectId,
            @PathVariable UUID todoId,
            @RequestBody TodoRequestDTO todoRequestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails.getUsername();
        TodoResponseDTO updatedTodo = todoService.updateTodo(projectId,todoId, todoRequestDTO, username);
        return ResponseEntity.ok(updatedTodo);
    }
    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> deleteTodoFromProject(
            @PathVariable UUID projectId,
            @PathVariable UUID todoId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        todoService.deleteTodoFromProject(todoId, projectId, username);
        return ResponseEntity.noContent().build(); 
    }

}
