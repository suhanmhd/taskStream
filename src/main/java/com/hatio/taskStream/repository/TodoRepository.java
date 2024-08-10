package com.hatio.taskStream.repository;

import com.hatio.taskStream.model.Project;
import com.hatio.taskStream.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface TodoRepository extends JpaRepository<Todo, UUID> {
    List<Todo> findByProject(Project project);
}
