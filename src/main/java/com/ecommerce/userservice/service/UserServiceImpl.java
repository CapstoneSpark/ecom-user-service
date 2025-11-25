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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public UserResponse registerUser(RegisterRequest registerRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        // Create new user
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhone(registerRequest.getPhone());

        // Assign default CUSTOMER role
        Role customerRole = roleRepository.findByRoleName(RoleName.CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", "CUSTOMER"));
        
        Set<Role> roles = new HashSet<>();
        roles.add(customerRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        return mapToUserResponse(savedUser);
    }


    
    @Override
    public LoginResponse loginUser(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user from database
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // ✅ CHANGED: Pass userId to include in token
        String jwt = jwtUtils.generateJwtToken(authentication, user.getUserId());

        // Extract roles
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toSet());

        // Return response with token
        return new LoginResponse(
                jwt,
                "Bearer",
                user.getUserId(),
                user.getEmail(),
                roles
        );
    }

    @Override
    public UserResponse getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return mapToUserResponse(user);
    }

    @Override
    public UserResponse updateUserProfile(String email, UpdateProfileRequest updateProfileRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        user.setFirstName(updateProfileRequest.getFirstName());
        user.setLastName(updateProfileRequest.getLastName());
        user.setPhone(updateProfileRequest.getPhone());

        User updatedUser = userRepository.save(user);

        return mapToUserResponse(updatedUser);
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        return mapToUserResponse(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::mapToUserResponse);
    }

    @Override
    public UserResponse assignRoleToUser(Long userId, RoleRequest roleRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        try {
            RoleName roleName = RoleName.valueOf(roleRequest.getRoleName().toUpperCase());
            Role role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", roleRequest.getRoleName()));

            if (user.getRoles().contains(role)) {
                throw new BadRequestException("User already has this role");
            }

            user.getRoles().add(role);
            User updatedUser = userRepository.save(user);

            return mapToUserResponse(updatedUser);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role name: " + roleRequest.getRoleName());
        }
    }

    @Override
    public UserResponse removeRoleFromUser(Long userId, RoleRequest roleRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        try {
            RoleName roleName = RoleName.valueOf(roleRequest.getRoleName().toUpperCase());
            Role role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "roleName", roleRequest.getRoleName()));

            if (!user.getRoles().contains(role)) {
                throw new BadRequestException("User does not have this role");
            }

            if (user.getRoles().size() == 1) {
                throw new BadRequestException("Cannot remove the last role from user");
            }

            user.getRoles().remove(role);
            User updatedUser = userRepository.save(user);

            return mapToUserResponse(updatedUser);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role name: " + roleRequest.getRoleName());
        }
    }

    // Helper method to map User entity to UserResponse DTO
    private UserResponse mapToUserResponse(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                roles,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}