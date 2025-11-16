package com.authpackage.authentication.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_username", columnList = "username")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = false;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean accountNonLocked = true;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean accountNonExpired = true;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean credentialsNonExpired = true;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;
    
    private String emailVerificationToken;
    
    private LocalDateTime emailVerificationTokenExpiry;
    
    private String passwordResetToken;
    
    private LocalDateTime passwordResetTokenExpiry;
    
    private LocalDateTime lastLogin;
    
    private String lastLoginIp;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public void addRole(Role role) {
        this.roles.add(role);
    }
    
    public void removeRole(Role role) {
        this.roles.remove(role);
    }
    
    public boolean hasRole(String roleName) {
        return roles.stream()
            .anyMatch(role -> role.getName().equals(roleName));
    }
}

