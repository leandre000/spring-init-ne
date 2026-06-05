package com.utility.billing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO representing a user creation payload submitted by an administrator.
 */
@Getter
@Setter
public class AdminUserCreateRequest extends RegisterRequest {

    @NotBlank(message = "Role name is required")
    private String roleName;
}
