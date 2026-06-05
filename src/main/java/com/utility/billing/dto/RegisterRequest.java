package com.utility.billing.dto;

import com.utility.billing.common.validation.ValidationMessages;
import com.utility.billing.common.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO representing a user registration payload.
 */
@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = ValidationMessages.REQUIRED)
    @Size(max = 100, message = ValidationMessages.FULL_NAME_TOO_LONG)
    private String fullName;

    @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
    @Email(message = ValidationMessages.EMAIL_INVALID)
    @Size(max = 254, message = ValidationMessages.EMAIL_TOO_LONG)
    private String email;

    @Size(max = 20, message = ValidationMessages.TOO_LONG)
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = ValidationMessages.PHONE_INVALID)
    private String phoneNumber;

    @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
    @ValidPassword
    private String password;
    
    @NotBlank(message = ValidationMessages.REQUIRED)
    private String roleName;
}
