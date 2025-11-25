package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.dto.*;
import com.ecommerce.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        testUserResponse = new UserResponse();
        testUserResponse.setUserId(1L);
        testUserResponse.setFirstName("John");
        testUserResponse.setLastName("Doe");
        testUserResponse.setEmail("john@example.com");
        testUserResponse.setRoles(new HashSet<>(Arrays.asList("CUSTOMER")));
    }

    @Test
    void registerUser_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPassword("Password@123");
        request.setPhone("9876543210");

        when(userService.registerUser(any(RegisterRequest.class))).thenReturn(testUserResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.registerUser(request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("john@example.com", response.getBody().getEmail());
    }

    @Test
    void loginUser_Success() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("Password@123");

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken("jwt-token");
        loginResponse.setUserId(1L);

        when(userService.loginUser(any(LoginRequest.class))).thenReturn(loginResponse);

        // Act
        ResponseEntity<LoginResponse> response = userController.loginUser(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody().getToken());
    }

    @Test
    void getUserProfile_Success() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("john@example.com");
        
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userService.getUserProfile("john@example.com")).thenReturn(testUserResponse);

        // Act
        ResponseEntity<UserResponse> response = userController.getUserProfile();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("john@example.com", response.getBody().getEmail());
    }
}