package com.example.securityjwt.service;

import com.example.securityjwt.dto.request.UserRequest;
import com.example.securityjwt.dto.response.UserResponse;
import com.example.securityjwt.entity.User;
import com.example.securityjwt.repository.UserRepository;
import com.example.securityjwt.mapper.UserMapper;
import com.example.securityjwt.util.AuthHelper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    AuthHelper authHelper;

    public UserResponse getUser(Long id) {
        log.info("Operation of getting user with ID {} started", id);
        User user = userRepository.findById(id).orElseThrow();
        UserResponse response = userMapper.toDto(user);
        log.info("User successfully returned");
        return response;
    }

    public UserResponse updateUser(UserRequest request) {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        log.info("Operation of updating user is started by user with ID {}", authenticatedUser.getId());
        userMapper.updateInfo(authenticatedUser, request);
        userRepository.save(authenticatedUser);
        UserResponse response = userMapper.toDto(authenticatedUser);
        log.info("User successfully updated");
        return response;
    }
}
