package com.utility.billing.dto;

import com.utility.billing.common.validation.ValidationMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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
    private String nationalId;

    @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
    @Email(message = ValidationMessages.EMAIL_INVALID)
    @Size(max = 254, message = ValidationMessages.EMAIL_TOO_LONG)
    private String email;

    @Size(max = 20, message = ValidationMessages.TOO_LONG)
    private String phoneNumber;

    private String address;

    private String status;
}
