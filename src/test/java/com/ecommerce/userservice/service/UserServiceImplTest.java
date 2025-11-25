//package com.ecommerce.userservice.service;
//
//import com.ecommerce.userservice.dto.*;
//import com.ecommerce.userservice.exception.BadRequestException;
//import com.ecommerce.userservice.exception.ResourceNotFoundException;
//import com.ecommerce.userservice.model.Role;
//import com.ecommerce.userservice.model.RoleName;
//import com.ecommerce.userservice.model.User;
//import com.ecommerce.userservice.repository.RoleRepository;
//import com.ecommerce.userservice.repository.UserRepository;
//import com.ecommerce.userservice.security.JwtUtils;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class UserServiceImplTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private RoleRepository roleRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @Mock
//    private AuthenticationManager authenticationManager;
//
//    @Mock
//    private JwtUtils jwtUtils;
//
//    @InjectMocks
//    private UserServiceImpl userService;
//
//    private User testUser;
//    private Role customerRole;
//    private Role adminRole;
//
//    @BeforeEach
//    void setUp() {
//        // Setup test user
//        testUser = new User();
//        testUser.setUserId(1L);
//        testUser.setFirstName("John");
//        testUser.setLastName("Doe");
//        testUser.setEmail("john@example.com");
//        testUser.setPasswordHash("encodedPassword");
//        testUser.setPhone("9876543210");
//
//        // Setup roles
//        customerRole = new Role();
//        customerRole.setRoleId(1L);
//        customerRole.setRoleName(RoleName.CUSTOMER);
//
//        adminRole = new Role();
//        adminRole.setRoleId(2L);
//        adminRole.setRoleName(RoleName.ADMIN);
//
//        Set<Role> roles = new HashSet<>();
//        roles.add(customerRole);
//        testUser.setRoles(roles);
//    }
//
//    @Test
//    void registerUser_Success() {
//        // Arrange
//        RegisterRequest request = new RegisterRequest();
//        request.setFirstName("John");
//        request.setLastName("Doe");
//        request.setEmail("john@example.com");
//        request.setPassword("password123");
//        request.setPhone("9876543210");
//
//        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
//        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
//        when(roleRepository.findByRoleName(RoleName.CUSTOMER)).thenReturn(Optional.of(customerRole));
//        when(userRepository.save(any(User.class))).thenReturn(testUser);
//
//        // Act
//        UserResponse response = userService.registerUser(request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals("John", response.getFirstName());
//        assertEquals("john@example.com", response.getEmail());
//        assertTrue(response.getRoles().contains("CUSTOMER"));
//
//        verify(userRepository).existsByEmail(request.getEmail());
//        verify(passwordEncoder).encode(request.getPassword());
//        verify(roleRepository).findByRoleName(RoleName.CUSTOMER);
//        verify(userRepository).save(any(User.class));
//    }
//
//    @Test
//    void registerUser_EmailAlreadyExists_ThrowsException() {
//        // Arrange
//        RegisterRequest request = new RegisterRequest();
//        request.setEmail("existing@example.com");
//
//        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
//
//        // Act & Assert
//        BadRequestException exception = assertThrows(
//                BadRequestException.class,
//                () -> userService.registerUser(request)
//        );
//
//        assertEquals("Email is already registered", exception.getMessage());
//        verify(userRepository).existsByEmail(request.getEmail());
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void loginUser_Success() {
//        LoginRequest request = new LoginRequest();
//        request.setEmail("john@example.com");
//        request.setPassword("password123");
//
//        Authentication authentication = mock(Authentication.class);
//
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
//                .thenReturn(authentication);
//
//        when(userRepository.findByEmail("john@example.com"))
//                .thenReturn(Optional.of(testUser));
//
//        when(jwtUtils.generateJwtToken(any(Authentication.class)))
//                .thenReturn("jwt-token");
//
//        LoginResponse response = userService.loginUser(request);
//
//        assertNotNull(response);
//        assertEquals("jwt-token", response.getToken());
//        assertEquals("Bearer", response.getType());
//        assertEquals(1L, response.getUserId());
//        assertEquals("john@example.com", response.getEmail());
//        assertTrue(response.getRoles().contains("CUSTOMER"));
//
//        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
//        verify(userRepository).findByEmail("john@example.com");
//        verify(jwtUtils).generateJwtToken(authentication);
//    }
//
//
//
//    @Test
//    void getUserProfile_Success() {
//        // Arrange
//        String email = "john@example.com";
//        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
//
//        // Act
//        UserResponse response = userService.getUserProfile(email);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals("John", response.getFirstName());
//        assertEquals("Doe", response.getLastName());
//        assertEquals(email, response.getEmail());
//        assertEquals("9876543210", response.getPhone());
//
//        verify(userRepository).findByEmail(email);
//    }
//
//    @Test
//    void getUserProfile_UserNotFound_ThrowsException() {
//        // Arrange
//        String email = "nonexistent@example.com";
//        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        ResourceNotFoundException exception = assertThrows(
//                ResourceNotFoundException.class,
//                () -> userService.getUserProfile(email)
//        );
//
//        assertTrue(exception.getMessage().contains("User not found"));
//        verify(userRepository).findByEmail(email);
//    }
//
//    @Test
//    void updateUserProfile_Success() {
//        // Arrange
//        String email = "john@example.com";
//        UpdateProfileRequest request = new UpdateProfileRequest();
//        request.setFirstName("Jane");
//        request.setLastName("Smith");
//        request.setPhone("9123456789");
//
//        User updatedUser = new User();
//        updatedUser.setUserId(1L);
//        updatedUser.setFirstName("Jane");
//        updatedUser.setLastName("Smith");
//        updatedUser.setEmail(email);
//        updatedUser.setPhone("9123456789");
//        updatedUser.setRoles(testUser.getRoles());
//
//        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
//        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
//
//        // Act
//        UserResponse response = userService.updateUserProfile(email, request);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals("Jane", response.getFirstName());
//        assertEquals("Smith", response.getLastName());
//        assertEquals("9123456789", response.getPhone());
//
//        verify(userRepository).findByEmail(email);
//        verify(userRepository).save(any(User.class));
//    }
//
//    @Test
//    void getUserById_Success() {
//        // Arrange
//        Long userId = 1L;
//        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
//
//        // Act
//        UserResponse response = userService.getUserById(userId);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(userId, response.getUserId());
//        assertEquals("John", response.getFirstName());
//
//        verify(userRepository).findById(userId);
//    }
//
//    @Test
//    void getUserById_UserNotFound_ThrowsException() {
//        // Arrange
//        Long userId = 999L;
//        when(userRepository.findById(userId)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        ResourceNotFoundException exception = assertThrows(
//                ResourceNotFoundException.class,
//                () -> userService.getUserById(userId)
//        );
//
//        assertTrue(exception.getMessage().contains("User not found"));
//        verify(userRepository).findById(userId);
//    }
//
//    @Test
//    void getAllUsers_Success() {
//        // Arrange
//        List<User> users = Arrays.asList(testUser);
//        Page<User> userPage = new PageImpl<>(users);
//        Pageable pageable = PageRequest.of(0, 10);
//
//        when(userRepository.findAll(pageable)).thenReturn(userPage);
//
//        // Act
//        Page<UserResponse> response = userService.getAllUsers(pageable);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(1, response.getTotalElements());
//        assertEquals("John", response.getContent().get(0).getFirstName());
//
//        verify(userRepository).findAll(pageable);
//    }
//
//    @Test
//    void assignRoleToUser_Success() {
//        // Arrange
//        Long userId = 1L;
//        RoleRequest request = new RoleRequest();
//        request.setRoleName("ADMIN");
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
//        when(roleRepository.findByRoleName(RoleName.ADMIN)).thenReturn(Optional.of(adminRole));
//        when(userRepository.save(any(User.class))).thenReturn(testUser);
//
//        // Act
//        UserResponse response = userService.assignRoleToUser(userId, request);
//
//        // Assert
//        assertNotNull(response);
//        assertTrue(testUser.getRoles().contains(adminRole));
//
//        verify(userRepository).findById(userId);
//        verify(roleRepository).findByRoleName(RoleName.ADMIN);
//        verify(userRepository).save(testUser);
//    }
//
//    @Test
//    void assignRoleToUser_UserAlreadyHasRole_ThrowsException() {
//        // Arrange
//        Long userId = 1L;
//        RoleRequest request = new RoleRequest();
//        request.setRoleName("CUSTOMER");
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
//        when(roleRepository.findByRoleName(RoleName.CUSTOMER)).thenReturn(Optional.of(customerRole));
//
//        // Act & Assert
//        BadRequestException exception = assertThrows(
//                BadRequestException.class,
//                () -> userService.assignRoleToUser(userId, request)
//        );
//
//        assertEquals("User already has this role", exception.getMessage());
//        verify(userRepository).findById(userId);
//        verify(roleRepository).findByRoleName(RoleName.CUSTOMER);
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void removeRoleFromUser_Success() {
//        // Arrange
//        Long userId = 1L;
//        testUser.getRoles().add(adminRole); // Add admin role so user has 2 roles
//        
//        RoleRequest request = new RoleRequest();
//        request.setRoleName("ADMIN");
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
//        when(roleRepository.findByRoleName(RoleName.ADMIN)).thenReturn(Optional.of(adminRole));
//        when(userRepository.save(any(User.class))).thenReturn(testUser);
//
//        // Act
//        UserResponse response = userService.removeRoleFromUser(userId, request);
//
//        // Assert
//        assertNotNull(response);
//        assertFalse(testUser.getRoles().contains(adminRole));
//
//        verify(userRepository).findById(userId);
//        verify(roleRepository).findByRoleName(RoleName.ADMIN);
//        verify(userRepository).save(testUser);
//    }
//
//    @Test
//    void removeRoleFromUser_LastRole_ThrowsException() {
//        // Arrange
//        Long userId = 1L;
//        RoleRequest request = new RoleRequest();
//        request.setRoleName("CUSTOMER");
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
//        when(roleRepository.findByRoleName(RoleName.CUSTOMER)).thenReturn(Optional.of(customerRole));
//
//        // Act & Assert
//        BadRequestException exception = assertThrows(
//                BadRequestException.class,
//                () -> userService.removeRoleFromUser(userId, request)
//        );
//
//        assertEquals("Cannot remove the last role from user", exception.getMessage());
//        verify(userRepository).findById(userId);
//        verify(roleRepository).findByRoleName(RoleName.CUSTOMER);
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void assignRoleToUser_InvalidRoleName_ThrowsException() {
//        // Arrange
//        Long userId = 1L;
//        RoleRequest request = new RoleRequest();
//        request.setRoleName("INVALID_ROLE");
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
//
//        // Act & Assert
//        BadRequestException exception = assertThrows(
//                BadRequestException.class,
//                () -> userService.assignRoleToUser(userId, request)
//        );
//
//        assertTrue(exception.getMessage().contains("Invalid role name"));
//        verify(userRepository).findById(userId);
//        verify(userRepository, never()).save(any(User.class));
//    }
//}

package com.ecommerce.userservice.service;

import com.ecommerce.userservice.dto.*;
import com.ecommerce.userservice.exception.BadRequestException;
import com.ecommerce.userservice.exception.ResourceNotFoundException;
import com.ecommerce.userservice.model.Role;
import com.ecommerce.userservice.model.RoleName;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.RoleRepository;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtils jwtUtils;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Role customerRole;
    private Role adminRole;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john@example.com");
        testUser.setPasswordHash("encodedPassword");
        testUser.setPhone("9876543210");

        customerRole = new Role();
        customerRole.setRoleId(1L);
        customerRole.setRoleName(RoleName.CUSTOMER);

        adminRole = new Role();
        adminRole.setRoleId(2L);
        adminRole.setRoleName(RoleName.ADMIN);

        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        testUser.setRoles(roles);
    }

    @Test
    void registerUser_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPassword("password123");
        request.setPhone("9876543210");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName(RoleName.CUSTOMER)).thenReturn(Optional.of(customerRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.registerUser(request);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("john@example.com", response.getEmail());
        assertTrue(response.getRoles().contains("CUSTOMER"));

        verify(userRepository).existsByEmail(request.getEmail());
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> userService.registerUser(request));

        assertEquals("Email is already registered", ex.getMessage());
    }

    @Test
    void loginUser_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("password123");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(testUser));

        when(jwtUtils.generateJwtToken(any(Authentication.class), eq(1L)))
                .thenReturn("jwt-token");

        LoginResponse response = userService.loginUser(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals(1L, response.getUserId());

        verify(jwtUtils).generateJwtToken(eq(authentication), eq(1L));
    }

    @Test
    void getUserProfile_Success() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserProfile("john@example.com");

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
    }

    @Test
    void getUserProfile_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserProfile("unknown@example.com"));
    }

    @Test
    void updateUserProfile_Success() {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setFirstName("Jane");
        req.setLastName("Smith");
        req.setPhone("9123456789");

        User updated = new User();
        updated.setUserId(1L);
        updated.setFirstName("Jane");
        updated.setLastName("Smith");
        updated.setEmail("john@example.com");
        updated.setPhone("9123456789");
        updated.setRoles(testUser.getRoles());

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updated);

        UserResponse res = userService.updateUserProfile("john@example.com", req);

        assertEquals("Jane", res.getFirstName());
    }

    @Test
    void getUserById_UserNotFound_ThrowsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(999L));
    }

    @Test
    void getAllUsers_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(Collections.singletonList(testUser));

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<UserResponse> res = userService.getAllUsers(pageable);

        assertEquals(1, res.getTotalElements());
    }

    @Test
    void assignRoleToUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByRoleName(RoleName.ADMIN)).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        RoleRequest req = new RoleRequest();
        req.setRoleName("ADMIN");

        UserResponse res = userService.assignRoleToUser(1L, req);

        assertTrue(testUser.getRoles().contains(adminRole));
    }

    @Test
    void removeRoleFromUser_LastRole_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findByRoleName(RoleName.CUSTOMER)).thenReturn(Optional.of(customerRole));

        RoleRequest req = new RoleRequest();
        req.setRoleName("CUSTOMER");

        assertThrows(BadRequestException.class,
                () -> userService.removeRoleFromUser(1L, req));
    }
}
