package com.ecommerce.userservice.integration;

import com.ecommerce.userservice.dto.RegisterRequest;
import com.ecommerce.userservice.model.Role;
import com.ecommerce.userservice.model.RoleName;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.RoleRepository;
import com.ecommerce.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Role customerRole;

    @BeforeEach
    void setUp() {
        // Create roles if not exist
        if (roleRepository.findByRoleName(RoleName.CUSTOMER).isEmpty()) {
            customerRole = new Role();
            customerRole.setRoleName(RoleName.CUSTOMER);
            customerRole = roleRepository.save(customerRole);
        } else {
            customerRole = roleRepository.findByRoleName(RoleName.CUSTOMER).get();
        }
    }

    @Test
    void userRegistrationFlow_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPassword("Password@123");
        request.setPhone("9876543210");

        // Act - Create user
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        user.setRoles(roles);
        
        User savedUser = userRepository.save(user);

        // Assert
        assertNotNull(savedUser.getUserId());
        assertEquals("john@example.com", savedUser.getEmail());
        assertTrue(passwordEncoder.matches("Password@123", savedUser.getPasswordHash()));
        assertEquals(1, savedUser.getRoles().size());
    }

    @Test
    void userLoginFlow_PasswordVerification() {
        // Arrange - Create user
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setPasswordHash(passwordEncoder.encode("Password@123"));
        user.setPhone("9876543210");
        
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        user.setRoles(roles);
        
        userRepository.save(user);

        // Act - Verify password
        User foundUser = userRepository.findByEmail("test@example.com").orElse(null);

        // Assert
        assertNotNull(foundUser);
        assertTrue(passwordEncoder.matches("Password@123", foundUser.getPasswordHash()));
        assertFalse(passwordEncoder.matches("WrongPassword", foundUser.getPasswordHash()));
    }

    @Test
    void userProfileUpdate_Success() {
        // Arrange - Create user
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPasswordHash(passwordEncoder.encode("Password@123"));
        user.setPhone("9876543210");
        
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        user.setRoles(roles);
        
        user = userRepository.save(user);

        // Act - Update profile
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setPhone("9123456789");
        
        User updatedUser = userRepository.save(user);

        // Assert
        assertEquals("Jane", updatedUser.getFirstName());
        assertEquals("Smith", updatedUser.getLastName());
        assertEquals("9123456789", updatedUser.getPhone());
        assertNotNull(updatedUser.getUpdatedAt());
    }
}