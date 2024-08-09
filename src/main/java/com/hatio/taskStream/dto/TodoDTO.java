package com.hatio.taskStream.dto;

import com.hatio.taskStream.enums.TodoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDTO {
    private UUID id;
    private String description;
    private TodoStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
