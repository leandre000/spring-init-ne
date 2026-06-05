package com.utility.billing.service.impl;

import com.utility.billing.dto.AuthResponse;
import com.utility.billing.dto.LoginRequest;
import com.utility.billing.dto.RegisterRequest;
import com.utility.billing.dto.UserDto;
import com.utility.billing.entity.Role;
import com.utility.billing.entity.User;
import com.utility.billing.exception.DuplicateResourceException;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.exception.UnauthorizedException;
import com.utility.billing.mapper.UserMapper;
import com.utility.billing.repository.RoleRepository;
import com.utility.billing.repository.UserRepository;
import com.utility.billing.security.JwtService;
import com.utility.billing.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        Role role = roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", request.getRoleName()));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .status("ACTIVE")
                .role(role)
                .build();

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

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
}
