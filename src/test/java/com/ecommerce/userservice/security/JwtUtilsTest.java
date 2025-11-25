//package com.ecommerce.userservice.security;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.Collections;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class JwtUtilsTest {
//
//    private JwtUtils jwtUtils;
//
//    // Use a valid base64 encoded secret key for HS512 (minimum 512 bits / 64 bytes)
//    private final String testSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970337336763979244226452948404D635166546A576E5A7234753778214125442A";
//    private final long testExpiration = 3600000L; // 1 hour
//
//    @BeforeEach
//    void setUp() {
//        jwtUtils = new JwtUtils();
//        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", testSecret);
//        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", testExpiration);
//    }
//
//    @Test
//    void generateJwtToken_CreatesValidToken() {
//        // Arrange
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                "test@example.com",
//                null,
//                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
//        );
//
//        // Act
//        String token = jwtUtils.generateJwtToken(authentication);
//
//        // Assert
//        assertNotNull(token);
//        assertFalse(token.isEmpty());
//        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
//    }
//
//    @Test
//    void getUserNameFromJwtToken_ReturnsCorrectUsername() {
//        // Arrange
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                "user@example.com",
//                null,
//                Collections.emptyList()
//        );
//        String token = jwtUtils.generateJwtToken(authentication);
//
//        // Act
//        String username = jwtUtils.getUserNameFromJwtToken(token);
//
//        // Assert
//        assertEquals("user@example.com", username);
//    }
//
//    @Test
//    void validateJwtToken_WithValidToken_ReturnsTrue() {
//        // Arrange
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                "test@example.com",
//                null,
//                Collections.emptyList()
//        );
//        String token = jwtUtils.generateJwtToken(authentication);
//
//        // Act
//        boolean isValid = jwtUtils.validateJwtToken(token);
//
//        // Assert
//        assertTrue(isValid);
//    }
//
//    @Test
//    void validateJwtToken_WithInvalidToken_ReturnsFalse() {
//        // Arrange
//        String invalidToken = "invalid.jwt.token";
//
//        // Act
//        boolean isValid = jwtUtils.validateJwtToken(invalidToken);
//
//        // Assert
//        assertFalse(isValid);
//    }
//
//    @Test
//    void validateJwtToken_WithMalformedToken_ReturnsFalse() {
//        // Arrange
//        String malformedToken = "notajwttoken";
//
//        // Act
//        boolean isValid = jwtUtils.validateJwtToken(malformedToken);
//
//        // Assert
//        assertFalse(isValid);
//    }
//
//    @Test
//    void validateJwtToken_WithExpiredToken_ReturnsFalse() throws InterruptedException {
//        // Arrange - Create JWT with very short expiration
//        JwtUtils shortExpirationJwt = new JwtUtils();
//        ReflectionTestUtils.setField(shortExpirationJwt, "jwtSecret", testSecret);
//        ReflectionTestUtils.setField(shortExpirationJwt, "jwtExpirationMs", 1L); // 1ms expiration
//
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                "test@example.com",
//                null,
//                Collections.emptyList()
//        );
//        String token = shortExpirationJwt.generateJwtToken(authentication);
//
//        // Wait for token to expire
//        Thread.sleep(10);
//
//        // Act
//        boolean isValid = shortExpirationJwt.validateJwtToken(token);
//
//        // Assert
//        assertFalse(isValid);
//    }
//
//    @Test
//    void generateJwtToken_PreservesUsername() {
//        // Arrange
//        String expectedUsername = "john.doe@example.com";
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                expectedUsername,
//                null,
//                Collections.emptyList()
//        );
//
//        // Act
//        String token = jwtUtils.generateJwtToken(authentication);
//        String extractedUsername = jwtUtils.getUserNameFromJwtToken(token);
//
//        // Assert
//        assertEquals(expectedUsername, extractedUsername);
//    }
//
//    @Test
//    void validateJwtToken_WithEmptyToken_ReturnsFalse() {
//        // Act
//        boolean isValid = jwtUtils.validateJwtToken("");
//
//        // Assert
//        assertFalse(isValid);
//    }
//
//    @Test
//    void validateJwtToken_WithNullToken_ReturnsFalse() {
//        // Act
//        boolean isValid = jwtUtils.validateJwtToken(null);
//
//        // Assert
//        assertFalse(isValid);
//    }
//}

package com.ecommerce.userservice.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    // Valid base64 secret key for HS512
    private final String testSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970337336763979244226452948404D635166546A576E5A7234753778214125442A";
    private final long testExpiration = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", testExpiration);
    }

    @Test
    void generateJwtToken_CreatesValidToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );

        String token = jwtUtils.generateJwtToken(authentication, 1L);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void getUserNameFromJwtToken_ReturnsCorrectUsername() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user@example.com",
                null,
                Collections.emptyList()
        );

        String token = jwtUtils.generateJwtToken(authentication, 99L);
        String username = jwtUtils.getUserNameFromJwtToken(token);

        assertEquals("user@example.com", username);
    }

    @Test
    void validateJwtToken_WithValidToken_ReturnsTrue() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com",
                null,
                Collections.emptyList()
        );

        String token = jwtUtils.generateJwtToken(authentication, 1L);
        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtToken_WithInvalidToken_ReturnsFalse() {
        String invalidToken = "invalid.jwt.token";
        assertFalse(jwtUtils.validateJwtToken(invalidToken));
    }

    @Test
    void validateJwtToken_WithMalformedToken_ReturnsFalse() {
        assertFalse(jwtUtils.validateJwtToken("notajwttoken"));
    }

    @Test
    void validateJwtToken_WithExpiredToken_ReturnsFalse() throws InterruptedException {
        JwtUtils shortExpirationJwt = new JwtUtils();
        ReflectionTestUtils.setField(shortExpirationJwt, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(shortExpirationJwt, "jwtExpirationMs", 1L); // 1ms

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "test@example.com",
                null,
                Collections.emptyList()
        );

        String token = shortExpirationJwt.generateJwtToken(authentication, 1L);
        Thread.sleep(10);

        assertFalse(shortExpirationJwt.validateJwtToken(token));
    }

    @Test
    void generateJwtToken_PreservesUsername() {
        String expectedUsername = "john.doe@example.com";

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                expectedUsername,
                null,
                Collections.emptyList()
        );

        String token = jwtUtils.generateJwtToken(authentication, 5L);
        String extractedUsername = jwtUtils.getUserNameFromJwtToken(token);

        assertEquals(expectedUsername, extractedUsername);
    }

    @Test
    void validateJwtToken_WithEmptyToken_ReturnsFalse() {
        assertFalse(jwtUtils.validateJwtToken(""));
    }

    @Test
    void validateJwtToken_WithNullToken_ReturnsFalse() {
        assertFalse(jwtUtils.validateJwtToken(null));
    }
}
