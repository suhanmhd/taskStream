package com.hatio.taskStream.auth.repositories;



import com.hatio.taskStream.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String username);


    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
