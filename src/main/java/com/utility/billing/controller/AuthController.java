package com.utility.billing.controller;

import com.utility.billing.common.ApiResponse;
import com.utility.billing.common.ResponseBuilder;
import com.utility.billing.dto.AuthResponse;
import com.utility.billing.dto.LoginRequest;
import com.utility.billing.dto.RegisterRequest;
import com.utility.billing.dto.UserDto;
import com.utility.billing.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.utility.billing.dto.ForgotPasswordRequest;
import com.utility.billing.dto.ResetPasswordRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller managing public authentication endpoints including registration, login, email verification, and password resets.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and JWT authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user account")
    public ResponseEntity<ApiResponse<UserDto>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpServletRequest) {
        UserDto dto = authService.register(request);
        return ResponseBuilder.created(dto, "Registration successful. Please verify your email.", httpServletRequest);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user credentials and issue access tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpServletRequest) {
        AuthResponse response = authService.login(request);
        return ResponseBuilder.ok(response, "Login successful", httpServletRequest);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renew an expired access token using a valid refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @RequestBody String refreshToken,
            HttpServletRequest httpServletRequest) {
        AuthResponse response = authService.refresh(refreshToken);
        return ResponseBuilder.ok(response, "Token refreshed successfully", httpServletRequest);
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify user email address using the registration token")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @io.swagger.v3.oas.annotations.Parameter(description = "The verification token string", required = true)
            @RequestParam String token,
            HttpServletRequest httpServletRequest) {
        authService.verifyEmail(token);
        return ResponseBuilder.ok("Email verified successfully. You can now log in.", httpServletRequest);
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend verification email to a pending user")
    public ResponseEntity<ApiResponse<Void>> resendVerification(
            @io.swagger.v3.oas.annotations.Parameter(description = "The registered email address", required = true)
            @RequestParam String email,
            HttpServletRequest httpServletRequest) {
        authService.resendVerification(email);
        return ResponseBuilder.ok("Verification email resent successfully", httpServletRequest);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request a password reset link")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpServletRequest) {
        authService.forgotPassword(request.getEmail());
        return ResponseBuilder.ok("If the email is registered, a password reset link has been sent.", httpServletRequest);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using the reset token")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpServletRequest) {
        authService.resetPassword(request.getToken(), request.getPassword());
        return ResponseBuilder.ok("Password reset successfully.", httpServletRequest);
    }

    @PutMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a user's role (Admin only)")
    public ResponseEntity<ApiResponse<UserDto>> updateUserRole(
            @PathVariable Long userId,
            @RequestParam String roleName,
            HttpServletRequest httpServletRequest) {
        UserDto dto = authService.updateUserRole(userId, roleName);
        return ResponseBuilder.ok(dto, "User role updated successfully", httpServletRequest);
    }
}
