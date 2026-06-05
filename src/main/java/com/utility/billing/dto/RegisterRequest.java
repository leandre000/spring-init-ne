package com.utility.billing.dto;

import com.utility.billing.common.validation.ValidationMessages;
import com.utility.billing.common.validation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "User Registration Request payload")
public class RegisterRequest {

    @NotBlank(message = ValidationMessages.REQUIRED)
    @Size(max = 100, message = ValidationMessages.FULL_NAME_TOO_LONG)
    @Schema(description = "Full name of the user", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fullName;

    @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
    @Email(message = ValidationMessages.EMAIL_INVALID)
    @Size(max = 254, message = ValidationMessages.EMAIL_TOO_LONG)
    @Schema(description = "Email address of the user (serves as username)", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Size(max = 20, message = ValidationMessages.TOO_LONG)
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = ValidationMessages.PHONE_INVALID)
    @Schema(description = "Phone number of the user", example = "+250780000000")
    private String phoneNumber;

    @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
    @ValidPassword
    @Schema(description = "Password of the user", example = "Secret@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Pattern(regexp = "^(ACTIVE|INACTIVE)$", message = "Status must be either ACTIVE or INACTIVE")
    @Schema(description = "Status of the user (optional, defaults to ACTIVE for admin-created users)", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE"})
    private String status;
}
