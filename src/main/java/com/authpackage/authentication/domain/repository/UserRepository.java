package com.authpackage.authentication.domain.repository;

import com.authpackage.authentication.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmailOrUsername(String email, String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    Optional<User> findByEmailVerificationToken(String token);
    
    Optional<User> findByPasswordResetToken(String token);
}

