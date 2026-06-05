package com.utility.billing.dto;

import com.utility.billing.common.validation.ValidationMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO representing a customer creation or update payload.
 */
@Getter
@Setter
public class CustomerRequest {

    @NotBlank(message = ValidationMessages.CUSTOMER_CODE_REQUIRED)
    @Size(max = 50, message = ValidationMessages.TOO_LONG)
    private String customerCode;

    @NotBlank(message = ValidationMessages.FULL_NAME_REQUIRED)
    @Size(max = 100, message = ValidationMessages.FULL_NAME_TOO_LONG)
    private String fullName;

    @NotBlank(message = ValidationMessages.NATIONAL_ID_REQUIRED)
    @Size(max = 50, message = ValidationMessages.TOO_LONG)
    @Pattern(regexp = "^\\d{16}$", message = "National ID must be exactly 16 digits")
    private String nationalId;

    @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
    @Email(message = ValidationMessages.EMAIL_INVALID)
    @Size(max = 254, message = ValidationMessages.EMAIL_TOO_LONG)
    private String email;

    @Size(max = 20, message = ValidationMessages.TOO_LONG)
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = ValidationMessages.PHONE_INVALID)
    private String phoneNumber;

    private String address;

    private String status;
}
