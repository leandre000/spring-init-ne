package com.utility.billing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO representing a user creation payload submitted by an administrator.
 */
@Getter
@Setter
@Schema(description = "Administrator User Creation Request payload")
public class AdminUserCreateRequest extends RegisterRequest {

    @NotBlank(message = "Role name is required")
    @Schema(description = "Role to be assigned to the user", example = "ROLE_OPERATOR", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleName;
}
