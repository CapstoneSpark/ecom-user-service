

package com.ecommerce.userservice.security;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

//import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * JWT Utils for User Service
 * 
 * This version stores roles INSIDE the JWT token during login.
 * Other services can extract roles without calling User Service database.
 */
@Component
public class JwtUtils {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private long jwtExpirationMs;
	
//	private final Dotenv dotenv = Dotenv.load();
//
//    private final String jwtSecret = dotenv.get("JWT_SECRET");
//    private final long jwtExpirationMs = Long.parseLong(dotenv.get("JWT_EXPIRATION"));

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate JWT token with roles embedded
     * Called during login to create token with user info
     */
    public String generateJwtToken(Authentication authentication, Long userId) {
        String username = authentication.getName();
        
        // Extract roles from authentication (includes "ROLE_" prefix)
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(username)                    // Email/username
                .claim("userId", userId)              // User ID
                .claim("roles", roles)                // Roles: ["ROLE_ADMIN", "ROLE_CUSTOMER"]
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Extract username from JWT token
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Extract user ID from JWT token
     */
    public Long getUserIdFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.get("userId", Long.class);
    }

    /**
     * Extract roles from JWT token
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromJwtToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.get("roles", List.class);
    }

    /**
     * Validate JWT token
     */
    public boolean validateJwtToken(String authToken) {
        try {
            if (authToken == null || authToken.trim().isEmpty()) {
                return false;
            }
            
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (JwtException e) {
            System.err.println("❌ Invalid JWT: " + e.getMessage());
        }
        return false;
    }
}