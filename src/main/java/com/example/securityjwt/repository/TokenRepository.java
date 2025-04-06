package com.example.securityjwt.repository;

import com.example.securityjwt.entity.Token;
import com.example.securityjwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUserAndIsLoggedOut(User user, Boolean isLoggedOut);

    Optional<Token> findByAccessToken(String accessToken);
}
