package com.hatio.taskStream.model;

import com.hatio.taskStream.auth.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid2")
    @GenericGenerator(name = "id", strategy = "uuid2")
    private UUID id;

    private String title;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @OneToMany(mappedBy="project",cascade=CascadeType.ALL)
    private List<Todo>todos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


}







