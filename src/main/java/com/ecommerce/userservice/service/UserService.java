package com.ecommerce.userservice.service;

import com.ecommerce.userservice.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse registerUser(RegisterRequest registerRequest);

    LoginResponse loginUser(LoginRequest loginRequest);

    UserResponse getUserProfile(String email);

    UserResponse updateUserProfile(String email, UpdateProfileRequest updateProfileRequest);

    UserResponse getUserById(Long userId);

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse assignRoleToUser(Long userId, RoleRequest roleRequest);

    UserResponse removeRoleFromUser(Long userId, RoleRequest roleRequest);
}