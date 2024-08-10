package com.hatio.taskStream.dto;

import com.hatio.taskStream.enums.TodoStatus;
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
    @NotNull
    private String description;
    @NotNull
    private TodoStatus status;
    @NotNull
    private UUID projectId;
}
