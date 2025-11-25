package com.ecommerce.userservice.config;

import com.ecommerce.userservice.model.Role;
import com.ecommerce.userservice.model.RoleName;
import com.ecommerce.userservice.model.User;
import com.ecommerce.userservice.repository.RoleRepository;
import com.ecommerce.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
        seedAdminUsers();
    }

    private void seedRoles() {
        logger.info("Seeding roles...");

        // Check and create CUSTOMER role
        if (roleRepository.findByRoleName(RoleName.CUSTOMER).isEmpty()) {
            Role customerRole = new Role(RoleName.CUSTOMER);
            roleRepository.save(customerRole);
            logger.info("✓ CUSTOMER role created");
        } else {
            logger.info("✓ CUSTOMER role already exists");
        }

        // Check and create ADMIN role
        if (roleRepository.findByRoleName(RoleName.ADMIN).isEmpty()) {
            Role adminRole = new Role(RoleName.ADMIN);
            roleRepository.save(adminRole);
            logger.info("✓ ADMIN role created");
        } else {
            logger.info("✓ ADMIN role already exists");
        }
    }

    private void seedAdminUsers() {
        logger.info("Seeding admin users...");

        Role adminRole = roleRepository.findByRoleName(RoleName.ADMIN)
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        Role customerRole = roleRepository.findByRoleName(RoleName.CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Customer role not found"));

        // Create first admin user
        if (!userRepository.existsByEmail("admin@ecommerce.com")) {
            User admin1 = new User();
            admin1.setFirstName("Admin");
            admin1.setLastName("User");
            admin1.setEmail("admin@ecommerce.com");
            admin1.setPasswordHash(passwordEncoder.encode("Admin@123"));
            admin1.setPhone("9876543210");

            Set<Role> admin1Roles = new HashSet<>();
            admin1Roles.add(adminRole);
            admin1Roles.add(customerRole);
            admin1.setRoles(admin1Roles);

            userRepository.save(admin1);
            logger.info("✓ Admin user created - Email: admin@ecommerce.com, Password: Admin@123");
        } else {
            logger.info("✓ Admin user (admin@ecommerce.com) already exists");
        }

        // Create second admin user
        if (!userRepository.existsByEmail("superadmin@ecommerce.com")) {
            User admin2 = new User();
            admin2.setFirstName("Super");
            admin2.setLastName("Admin");
            admin2.setEmail("superadmin@ecommerce.com");
            admin2.setPasswordHash(passwordEncoder.encode("SuperAdmin@123"));
            admin2.setPhone("9123456789");

            Set<Role> admin2Roles = new HashSet<>();
            admin2Roles.add(adminRole);
            admin2Roles.add(customerRole);
            admin2.setRoles(admin2Roles);

            userRepository.save(admin2);
            logger.info("✓ Super Admin user created - Email: superadmin@ecommerce.com, Password: SuperAdmin@123");
        } else {
            logger.info("✓ Super Admin user (superadmin@ecommerce.com) already exists");
        }

        logger.info("Data seeding completed successfully!");
    }
}