package com.example.securityjwt.repository;

import com.example.securityjwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String username);

    Optional<User> findById(Long id);
}
