package com.ecommerce.userservice.dto;

import jakarta.validation.constraints.NotBlank;

public class RoleRequest {

    @NotBlank(message = "Role name is required")
    private String roleName;

    // Constructors
    public RoleRequest() {
    }

    public RoleRequest(String roleName) {
        this.roleName = roleName;
    }

    // Getters and Setters
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}