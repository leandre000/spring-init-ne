package com.utility.billing.service;

import com.utility.billing.dto.AuthResponse;
import com.utility.billing.dto.LoginRequest;
import com.utility.billing.dto.RegisterRequest;
import com.utility.billing.dto.UserDto;
import com.utility.billing.dto.AdminUserCreateRequest;

/**
 * Service interface managing user registrations, authentication, token refreshes,
 * and account verification/recovery flows.
 */
public interface AuthService {

    /**
     * Registers a new user account as PENDING and generates a verification email.
     *
     * @param request the registration details
     * @return the registered user details DTO
     */
    UserDto register(RegisterRequest request);

    /**
     * Authenticates a user login request and generates JWT tokens.
     *
     * @param request the login credentials
     * @return the authentication response containing JWT tokens
     */
    AuthResponse login(LoginRequest request);

    /**
     * Renews an expired access token using a valid refresh token.
     *
     * @param refreshToken the refresh token string
     * @return the renewed authentication response
     */
    AuthResponse refresh(String refreshToken);

    /**
     * Verifies the email token and activates the corresponding user account.
     *
     * @param token the verification token string
     */
    void verifyEmail(String token);

    /**
     * Regenerates and resends a verification token to the user email if not yet verified.
     *
     * @param email the user's email address
     */
    void resendVerification(String email);

    /**
     * Initiates password recovery. If the email exists, a password reset token is sent.
     *
     * @param email the user's email address
     */
    void forgotPassword(String email);

    /**
     * Completes password recovery using the provided token and new password value.
     *
     * @param token       the password reset token string
     * @param newPassword the new password to set
     */
    void resetPassword(String token, String newPassword);

    /**
     * Updates a user's role. Restricted to administrators.
     *
     * @param userId   the ID of the user to update
     * @param roleName the new role name to assign
     * @return the updated user details DTO
     */
    UserDto updateUserRole(Long userId, String roleName);

    /**
     * Registers a new user account by an administrator, defaulting to ACTIVE status.
     *
     * @param request the admin user creation payload
     * @return the created user details DTO
     */
    UserDto createUser(AdminUserCreateRequest request);
}
