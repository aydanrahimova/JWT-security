package com.example.securityjwt.mapper;

import com.example.securityjwt.dto.request.UserRequest;
import com.example.securityjwt.dto.response.UserResponse;
import com.example.securityjwt.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequest request);

    UserResponse toDto(User user);

    void updateInfo(@MappingTarget User user, UserRequest userRequest);
}
