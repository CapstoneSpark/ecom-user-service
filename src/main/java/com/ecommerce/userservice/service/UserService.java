package com.ecommerce.userservice.service;

import com.ecommerce.userservice.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    // Authentication & Registration
    UserResponse registerUser(RegisterRequest registerRequest);
    
    LoginResponse loginUser(LoginRequest loginRequest);

    // Profile Management
    UserResponse getUserProfile(String email);
    
    UserResponse updateUserProfile(String email, UpdateProfileRequest updateProfileRequest);

    // Password Management
    ApiResponse resetPassword(Simpleresetpasswordrequest request);
    
    ApiResponse changePassword(String email, ChangePasswordRequest changePasswordRequest);

    // Admin Operations
    UserResponse getUserById(Long userId);
    
    Page<UserResponse> getAllUsers(Pageable pageable);
    
    UserResponse assignRoleToUser(Long userId, RoleRequest roleRequest);
    
    UserResponse removeRoleFromUser(Long userId, RoleRequest roleRequest);
    
    ApiResponse deleteUser(String email);

}