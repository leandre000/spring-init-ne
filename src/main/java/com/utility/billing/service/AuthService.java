package com.utility.billing.service;

import com.utility.billing.dto.AuthResponse;
import com.utility.billing.dto.LoginRequest;
import com.utility.billing.dto.RegisterRequest;
import com.utility.billing.dto.UserDto;

public interface AuthService {
    UserDto register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(String refreshToken);
}
