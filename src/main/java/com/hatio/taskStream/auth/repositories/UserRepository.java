package com.hatio.taskStream.auth.repositories;



import com.hatio.taskStream.auth.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String username);


    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
