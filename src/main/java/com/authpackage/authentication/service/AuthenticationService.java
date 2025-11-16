package com.authpackage.authentication.service;

import com.authpackage.authentication.domain.model.RefreshToken;
import com.authpackage.authentication.domain.model.Role;
import com.authpackage.authentication.domain.model.User;
import com.authpackage.authentication.domain.repository.RefreshTokenRepository;
import com.authpackage.authentication.domain.repository.RoleRepository;
import com.authpackage.authentication.domain.repository.UserRepository;
import com.authpackage.authentication.dto.request.*;
import com.authpackage.authentication.dto.response.AuthResponse;
import com.authpackage.authentication.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordService passwordService;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    
    @Value("${auth.email-verification.enabled:true}")
    private boolean emailVerificationEnabled;
    
    @Value("${auth.email-verification.expiry-hours:24}")
    private int emailVerificationExpiryHours;
    
    @Value("${auth.password-reset.expiry-hours:1}")
    private int passwordResetExpiryHours;
    
    @Value("${auth.refresh-token.expiry-days:7}")
    private int refreshTokenExpiryDays;
    
    public AuthenticationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordService passwordService,
            JwtService jwtService,
            TokenService tokenService,
            EmailService emailService,
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered: " + request.getEmail());
        }
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken: " + request.getUsername());
        }
        
        User user = User.builder()
            .email(request.getEmail())
            .username(request.getUsername())
            .password(passwordService.encode(request.getPassword()))
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .enabled(!emailVerificationEnabled)
            .emailVerified(!emailVerificationEnabled)
            .build();
        
        // Assign default role if exists
        roleRepository.findByName("ROLE_USER")
            .ifPresent(user::addRole);
        
        user = userRepository.save(user);
        
        // Send verification email if enabled
        if (emailVerificationEnabled) {
            String verificationToken = tokenService.generateToken();
            user.setEmailVerificationToken(verificationToken);
            user.setEmailVerificationTokenExpiry(LocalDateTime.now().plusHours(emailVerificationExpiryHours));
            userRepository.save(user);
            emailService.sendVerificationEmail(user.getEmail(), verificationToken);
        }
        
        return buildAuthResponse(user, null);
    }
    
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmailOrUsername(),
                    request.getPassword()
                )
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email/username or password");
        }
        
        User user = userRepository.findByEmailOrUsername(
            request.getEmailOrUsername(),
            request.getEmailOrUsername()
        ).orElseThrow(() -> new UserNotFoundException("User not found"));
        
        if (!user.getEnabled()) {
            throw new AccountDisabledException("Account is disabled. Please verify your email.");
        }
        
        if (!user.getAccountNonLocked()) {
            throw new AccountLockedException("Account is locked");
        }
        
        // Update last login
        user.setLastLogin(LocalDateTime.now());
        user.setLastLoginIp(getClientIpAddress(httpRequest));
        userRepository.save(user);
        
        // Create refresh token
        RefreshToken refreshToken = createRefreshToken(user, httpRequest);
        
        return buildAuthResponse(user, refreshToken.getToken());
    }
    
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
            .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));
        
        if (!refreshToken.isValid()) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidTokenException("Refresh token is expired or revoked");
        }
        
        User user = refreshToken.getUser();
        
        if (!user.getEnabled()) {
            throw new AccountDisabledException("Account is disabled");
        }
        
        // Revoke old token and create new one
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        
        RefreshToken newRefreshToken = createRefreshToken(user, httpRequest);
        
        return buildAuthResponse(user, newRefreshToken.getToken());
    }
    
    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        User user = userRepository.findByEmailVerificationToken(request.getToken())
            .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));
        
        if (user.getEmailVerificationTokenExpiry() == null ||
            LocalDateTime.now().isAfter(user.getEmailVerificationTokenExpiry())) {
            throw new TokenExpiredException("Verification token has expired");
        }
        
        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiry(null);
        userRepository.save(user);
    }
    
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.getEmail()));
        
        String resetToken = tokenService.generateToken();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(passwordResetExpiryHours));
        userRepository.save(user);
        
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
    }
    
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByPasswordResetToken(request.getToken())
            .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));
        
        if (user.getPasswordResetTokenExpiry() == null ||
            LocalDateTime.now().isAfter(user.getPasswordResetTokenExpiry())) {
            throw new TokenExpiredException("Reset token has expired");
        }
        
        user.setPassword(passwordService.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
        
        // Revoke all refresh tokens for security
        refreshTokenRepository.revokeAllUserTokens(user);
    }
    
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
            .ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
    }
    
    private RefreshToken createRefreshToken(User user, HttpServletRequest request) {
        RefreshToken refreshToken = RefreshToken.builder()
            .token(tokenService.generateToken(64))
            .user(user)
            .expiryDate(LocalDateTime.now().plusDays(refreshTokenExpiryDays))
            .ipAddress(request != null ? getClientIpAddress(request) : "unknown")
            .userAgent(request != null ? request.getHeader("User-Agent") : "unknown")
            .build();
        
        return refreshTokenRepository.save(refreshToken);
    }
    
    private AuthResponse buildAuthResponse(User user, String refreshTokenValue) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        
        String accessToken = jwtService.generateToken(userDetails);
        
        if (refreshTokenValue == null) {
            RefreshToken refreshToken = createRefreshToken(user, null);
            refreshTokenValue = refreshToken.getToken();
        }
        
        Set<String> roles = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toSet());
        
        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshTokenValue)
            .expiresIn(jwtService.extractExpiration(accessToken).getTime() / 1000)
            .user(AuthResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roles)
                .emailVerified(user.getEmailVerified())
                .build())
            .build();
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}

