package com.ecommerce.userservice.dto;

import java.util.Set;

public class LoginResponse {

    private String token;
    private String type = "Bearer";
    private Long userId;
    private String email;
    private Set<String> roles;

    // Constructors
    public LoginResponse() {
    }

    public LoginResponse(String token, Long userId, String email, Set<String> roles) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.roles = roles;
    }
    
    public LoginResponse(String token, String type, Long userId, String email, Set<String> roles) {
        this.token = token;
        this.type = type;
        this.userId = userId;
        this.email = email;
        this.roles = roles;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}