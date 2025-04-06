package com.example.securityjwt.service;

import com.example.securityjwt.auth.Role;
import com.example.securityjwt.dto.request.AuthRequest;
import com.example.securityjwt.dto.response.AuthResponse;
import com.example.securityjwt.dto.request.RefreshTokenRequest;
import com.example.securityjwt.dto.request.RegisterRequest;
import com.example.securityjwt.entity.Authority;
import com.example.securityjwt.entity.Token;
import com.example.securityjwt.entity.User;
import com.example.securityjwt.exception.AlreadyExistException;
import com.example.securityjwt.exception.ResourceNotFoundException;
import com.example.securityjwt.exception.UnauthorizedException;
import com.example.securityjwt.repository.AuthRepository;
import com.example.securityjwt.repository.TokenRepository;
import com.example.securityjwt.repository.UserRepository;
import com.example.securityjwt.util.JwtUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    JwtUtil jwtUtil;
    AuthenticationManager authenticationManager;
    AuthRepository authRepository;
    TokenRepository tokenRepository;

    @Transactional
    public AuthResponse register(@Valid RegisterRequest request) {
        log.info("Operation of registration started");

        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("Failed to register user: User with email {} already exists", request.getEmail());
            throw new AlreadyExistException("USER_ALREADY_EXISTS");
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .authorities(List.of(authRepository.findByRole(Role.USER).orElseGet(() -> {
                    Authority authority = Authority.builder()
                            .role(Role.USER)
                            .build();
                    return authRepository.save(authority);
                })))
                .build();

        userRepository.save(user);

        var jwtAccessToken = jwtUtil.generateAccessToken(user);
        var jwtRefreshToken = jwtUtil.generateRefreshToken(user);

        revokeAllTokensOfUser(user);
        saveUserToken(user, jwtAccessToken, jwtRefreshToken);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();

        log.info("User successfully registered");

        return authResponse;
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> {
            log.error("User not found,user should register");
            return new ResourceNotFoundException("USER_NOT_FOUND");
        });
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var accessToken = jwtUtil.generateAccessToken(user);
        var refreshToken = jwtUtil.generateRefreshToken(user);

        revokeAllTokensOfUser(user);
        saveUserToken(user, accessToken, refreshToken);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        log.info("User successfully logged in");

        return authResponse;
    }

    @Transactional
    public AuthResponse refreshAccessToken(RefreshTokenRequest tokenRequest) {
        String refreshToken = tokenRequest.getRefreshToken();
        String userEmail = jwtUtil.extractUsername(refreshToken);

        if (userEmail == null) {
            throw new UnauthorizedException("Invalid refresh token - no user found");
        }

        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!jwtUtil.isTokenValid(refreshToken, user)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        String newAccessToken = jwtUtil.generateAccessToken(user);
        revokeAllTokensOfUser(user);
        saveUserToken(user, newAccessToken, refreshToken);
        return new AuthResponse(newAccessToken, refreshToken);
    }


    private void saveUserToken(User user, String accessToken, String refreshToken) {
        Token token = Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(user)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllTokensOfUser(User user) {
        List<Token> tokens = tokenRepository.findByUserAndIsLoggedOut(user, Boolean.FALSE);
        tokens.forEach(token -> token.setIsLoggedOut(Boolean.TRUE));
        tokenRepository.saveAll(tokens);
    }

}
