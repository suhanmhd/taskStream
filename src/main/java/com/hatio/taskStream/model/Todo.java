package com.hatio.taskStream.model;

import com.hatio.taskStream.enums.TodoStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "id", strategy = "uuid2")
    private UUID id;
    private String description;
    @Enumerated(EnumType.STRING)
    private TodoStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    @ManyToOne
    @JoinColumn(name="project_id",nullable = false)
    private  Project project;
}
