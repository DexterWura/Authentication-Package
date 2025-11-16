package com.authpackage.authentication.config;

import com.authpackage.authentication.domain.model.Permission;
import com.authpackage.authentication.domain.model.Role;
import com.authpackage.authentication.domain.repository.PermissionRepository;
import com.authpackage.authentication.domain.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    
    public DataInitializer(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }
    
    @Override
    public void run(String... args) {
        initializeRolesAndPermissions();
    }
    
    private void initializeRolesAndPermissions() {
        // Create permissions
        List<String> permissionNames = Arrays.asList(
            "READ_USER", "WRITE_USER", "DELETE_USER",
            "READ_ROLE", "WRITE_ROLE", "DELETE_ROLE",
            "READ_PERMISSION", "WRITE_PERMISSION", "DELETE_PERMISSION"
        );
        
        for (String permName : permissionNames) {
            if (!permissionRepository.existsByName(permName)) {
                Permission permission = Permission.builder()
                    .name(permName)
                    .description("Permission to " + permName.toLowerCase().replace("_", " "))
                    .build();
                permissionRepository.save(permission);
            }
        }
        
        // Create default roles
        if (!roleRepository.existsByName("ROLE_USER")) {
            Role userRole = Role.builder()
                .name("ROLE_USER")
                .description("Default user role")
                .build();
            roleRepository.save(userRole);
        }
        
        if (!roleRepository.existsByName("ROLE_ADMIN")) {
            Role adminRole = Role.builder()
                .name("ROLE_ADMIN")
                .description("Administrator role with full access")
                .build();
            
            // Add all permissions to admin role
            permissionRepository.findAll().forEach(adminRole::addPermission);
            roleRepository.save(adminRole);
        }
        
        if (!roleRepository.existsByName("ROLE_MODERATOR")) {
            Role moderatorRole = Role.builder()
                .name("ROLE_MODERATOR")
                .description("Moderator role with limited admin access")
                .build();
            
            // Add read permissions to moderator
            permissionRepository.findAll().stream()
                .filter(p -> p.getName().startsWith("READ_"))
                .forEach(moderatorRole::addPermission);
            roleRepository.save(moderatorRole);
        }
    }
}

