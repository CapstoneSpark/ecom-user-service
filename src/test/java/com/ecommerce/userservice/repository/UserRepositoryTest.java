package com.ecommerce.userservice.repository;

import com.ecommerce.userservice.model.Role;
import com.ecommerce.userservice.model.RoleName;
import com.ecommerce.userservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role customerRole;

    @BeforeEach
    void setUp() {
        // Create and persist customer role
        customerRole = new Role();
        customerRole.setRoleName(RoleName.CUSTOMER);
        customerRole = roleRepository.save(customerRole);
    }

    @Test
    void findByEmail_WhenUserExists_ReturnsUser() {
        // Arrange
        User user = createTestUser("test@example.com");
        userRepository.save(user);

        // Act
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void existsByEmail_WhenUserExists_ReturnsTrue() {
        // Arrange
        User user = createTestUser("existing@example.com");
        userRepository.save(user);

        // Act
        Boolean exists = userRepository.existsByEmail("existing@example.com");

        // Assert
        assertTrue(exists);
    }

    @Test
    void save_CreatesNewUser() {
        // Arrange
        User user = createTestUser("new@example.com");

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertNotNull(savedUser.getUserId());
        assertEquals("new@example.com", savedUser.getEmail());
    }

    private User createTestUser(String email) {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail(email);
        user.setPasswordHash("hashedPassword");
        user.setPhone("9876543210");

        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        user.setRoles(roles);

        return user;
    }
}