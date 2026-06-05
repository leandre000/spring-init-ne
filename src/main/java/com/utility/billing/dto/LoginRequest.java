package com.utility.billing.dto;

import com.utility.billing.common.validation.ValidationMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
    @Email(message = ValidationMessages.EMAIL_INVALID)
    private String email;

    @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
    private String password;
}
