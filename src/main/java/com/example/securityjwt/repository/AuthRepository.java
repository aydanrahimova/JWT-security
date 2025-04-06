package com.example.securityjwt.repository;

import com.example.securityjwt.auth.Role;
import com.example.securityjwt.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Authority, Long> {
    Optional<Authority> findByRole(Role role);
}
