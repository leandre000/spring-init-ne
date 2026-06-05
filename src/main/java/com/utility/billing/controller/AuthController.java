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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseBuilder.created(dto, "Registration successful", httpServletRequest);
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
}
