package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.dto.*;
import com.ecommerce.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "APIs for user authentication and profile management")
public class UserController {

    @Autowired
    private UserService userService;

    // ==================== AUTHENTICATION ENDPOINTS ====================

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Register a new customer account")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        UserResponse userResponse = userService.registerUser(registerRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return JWT token")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.loginUser(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    // ==================== PASSWORD MANAGEMENT ENDPOINTS ====================

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password using email and new password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody Simpleresetpasswordrequest request) {
        ApiResponse response = userService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Change password", description = "Change password for authenticated user")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        ApiResponse response = userService.changePassword(email, changePasswordRequest);
        return ResponseEntity.ok(response);
    }

    // ==================== PROFILE ENDPOINTS ====================

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get user profile", description = "Get current authenticated user's profile")
    public ResponseEntity<UserResponse> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        UserResponse userResponse = userService.getUserProfile(email);
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update user profile", description = "Update current user's profile information")
    public ResponseEntity<UserResponse> updateUserProfile(
            @Valid @RequestBody UpdateProfileRequest updateProfileRequest) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        UserResponse userResponse = userService.updateUserProfile(email, updateProfileRequest);
        return ResponseEntity.ok(userResponse);
    }

    // ==================== ADMIN ENDPOINTS ====================

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get user by ID", description = "Get specific user details by ID (Admin only)")
    public ResponseEntity<UserResponse> getUserById(@PathVariable(name = "id") Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get all users", description = "Get list of all users with pagination (Admin only)")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Assign role to user", description = "Assign a new role to a user (Admin only)")
    public ResponseEntity<UserResponse> assignRoleToUser(
            @PathVariable(name = "id") Long id,
            @Valid @RequestBody RoleRequest roleRequest) {
        
        UserResponse userResponse = userService.assignRoleToUser(id, roleRequest);
        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Remove role from user", description = "Remove a role from a user (Admin only)")
    public ResponseEntity<UserResponse> removeRoleFromUser(
            @PathVariable(name = "id") Long id,
            @Valid @RequestBody RoleRequest roleRequest) {
        
        UserResponse userResponse = userService.removeRoleFromUser(id, roleRequest);
        return ResponseEntity.ok(userResponse);
    }
    
    @DeleteMapping("/profile")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete user account", description = "Delete current authenticated user")
    public ResponseEntity<ApiResponse> deleteUserProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        ApiResponse response = userService.deleteUser(email);

        return ResponseEntity.ok(response);
    }

    
}