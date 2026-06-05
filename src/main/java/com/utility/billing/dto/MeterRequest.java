package com.utility.billing.dto;

import com.utility.billing.common.validation.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO representing a meter installation payload.
 */
@Getter
@Setter
public class MeterRequest {

    @NotBlank(message = ValidationMessages.METER_NUMBER_REQUIRED)
    @Size(max = 50, message = ValidationMessages.TOO_LONG)
    @Pattern(regexp = "^[A-Z0-9\\-]{5,20}$", message = "Meter number must be alphanumeric and between 5 and 20 characters")
    private String meterNumber;

    @NotBlank(message = ValidationMessages.METER_TYPE_REQUIRED)
    private String meterType;

    private String status;

    @NotNull(message = ValidationMessages.REQUIRED)
    private Long customerId;
}
