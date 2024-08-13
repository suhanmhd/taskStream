package com.hatio.taskStream.dto;

import com.hatio.taskStream.enums.TodoStatus;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoRequestDTO {
    @NotNull(message = "task description is mandatory")
    private String description;
    @NotNull(message = "Todo status is mandatory : (PENDING or COMPLETED)")
    private TodoStatus status;
    @NotNull(message = "ProjectID is mandatory")
    private UUID projectId;
}
