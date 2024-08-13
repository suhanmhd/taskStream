package com.hatio.taskStream.repository;

import com.hatio.taskStream.auth.model.User;
import com.hatio.taskStream.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByUserUsername(String username);

    List<Project> findByUser(User user);
}
