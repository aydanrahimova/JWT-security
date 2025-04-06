package com.example.securityjwt.controller;

import com.example.securityjwt.dto.request.UserRequest;
import com.example.securityjwt.dto.response.UserResponse;
import com.example.securityjwt.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserController {
    UserService userService;

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PutMapping
    public UserResponse updateUser(@Valid @RequestBody UserRequest request) {
        return userService.updateUser(request);
    }
}
