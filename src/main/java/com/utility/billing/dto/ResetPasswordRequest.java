package com.utility.billing.dto;

import com.utility.billing.common.validation.ValidPassword;
import com.utility.billing.common.validation.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing a request to reset a user's password using a recovery token.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "Reset token is required")
    private String token;

    @NotBlank(message = ValidationMessages.PASSWORD_REQUIRED)
    @ValidPassword
    private String password;
}
