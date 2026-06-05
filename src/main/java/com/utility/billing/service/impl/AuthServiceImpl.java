package com.utility.billing.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.utility.billing.dto.AuthResponse;
import com.utility.billing.dto.LoginRequest;
import com.utility.billing.dto.RegisterRequest;
import com.utility.billing.dto.UserDto;
import com.utility.billing.entity.EmailVerificationToken;
import com.utility.billing.entity.PasswordResetToken;
import com.utility.billing.entity.Role;
import com.utility.billing.entity.User;
import com.utility.billing.exception.BusinessException;
import com.utility.billing.exception.DuplicateResourceException;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.exception.UnauthorizedException;
import com.utility.billing.mapper.UserMapper;
import com.utility.billing.repository.EmailVerificationTokenRepository;
import com.utility.billing.repository.PasswordResetTokenRepository;
import com.utility.billing.repository.RoleRepository;
import com.utility.billing.repository.UserRepository;
import com.utility.billing.security.JwtService;
import com.utility.billing.service.AuthService;
import com.utility.billing.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Service implementation handling user registration, JWT login, and token-based account recovery/validation.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Only allow ROLE_CUSTOMER registration publicly. Non-customer roles require ADMIN authentication.
        if (!"ROLE_CUSTOMER".equalsIgnoreCase(request.getRoleName())) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = authentication != null && authentication.isAuthenticated() &&
                    authentication.getAuthorities().stream()
                            .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            if (!isAdmin) {
                throw new BusinessException("Registration of staff roles is restricted to administrators", HttpStatus.FORBIDDEN);
            }
        }

        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", request.getRoleName()));

        // Initialized as PENDING, requiring email verification
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .status("PENDING")
                .role(role)
                .build();

        User savedUser = userRepository.save(user);

        // Generate and save registration verification token
        String tokenStr = UUID.randomUUID().toString();
        EmailVerificationToken token = EmailVerificationToken.builder()
                .token(tokenStr)
                .user(savedUser)
                .expiryDate(Instant.now().plus(24, ChronoUnit.HOURS))
                .build();
        emailVerificationTokenRepository.save(token);

        // Send email asynchronously
        emailService.sendVerificationEmail(savedUser, tokenStr);

        return userMapper.toDto(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        // Enforce email verification check before checking passwords
        if ("PENDING".equalsIgnoreCase(user.getStatus())) {
            throw new BusinessException("Email address is not verified", HttpStatus.FORBIDDEN);
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        if (!user.isEnabled()) {
            throw new UnauthorizedException("User account is inactive or suspended");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .role(user.getRole().getName())
                .expiresIn(86400)
                .build();
    }

    @Override
    public AuthResponse refresh(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);
        if (email == null) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .email(user.getEmail())
                .role(user.getRole().getName())
                .expiresIn(86400)
                .build();
    }

    @Override
    @Transactional
    public void verifyEmail(String tokenStr) {
        EmailVerificationToken token = emailVerificationTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new ResourceNotFoundException("Verification token", "value", tokenStr));

        if (token.isExpired()) {
            throw new BusinessException("Verification token has expired", HttpStatus.BAD_REQUEST);
        }

        User user = token.getUser();
        user.setStatus("ACTIVE");
        userRepository.save(user);
        emailVerificationTokenRepository.delete(token);
    }

    @Override
    @Transactional
    public void resendVerification(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if ("ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new BusinessException("Email address is already verified", HttpStatus.BAD_REQUEST);
        }

        // Delete any existing tokens to avoid duplicate rows
        emailVerificationTokenRepository.findByUser(user).ifPresent(emailVerificationTokenRepository::delete);

        String tokenStr = UUID.randomUUID().toString();
        EmailVerificationToken token = EmailVerificationToken.builder()
                .token(tokenStr)
                .user(user)
                .expiryDate(Instant.now().plus(24, ChronoUnit.HOURS))
                .build();
        emailVerificationTokenRepository.save(token);

        emailService.sendVerificationEmail(user, tokenStr);
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        // Return 200 silently even if user is not found to prevent user/email harvesting
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return;
        }

        // Clean up previous reset attempts
        passwordResetTokenRepository.findByUser(user).ifPresent(passwordResetTokenRepository::delete);

        String tokenStr = UUID.randomUUID().toString();
        PasswordResetToken token = PasswordResetToken.builder()
                .token(tokenStr)
                .user(user)
                .expiryDate(Instant.now().plus(15, ChronoUnit.MINUTES))
                .build();
        passwordResetTokenRepository.save(token);

        emailService.sendPasswordResetEmail(user, tokenStr);
    }

    @Override
    @Transactional
    public void resetPassword(String tokenStr, String newPassword) {
        PasswordResetToken token = passwordResetTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new ResourceNotFoundException("Password reset token", "value", tokenStr));

        if (token.isExpired()) {
            throw new BusinessException("Password reset token has expired", HttpStatus.BAD_REQUEST);
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(token);
    }

    @Override
    @Transactional
    public UserDto updateUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));

        user.setRole(role);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }
}
