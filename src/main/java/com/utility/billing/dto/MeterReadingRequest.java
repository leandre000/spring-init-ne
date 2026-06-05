package com.utility.billing.dto;

import com.utility.billing.common.validation.ValidationMessages;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Request DTO representing a meter reading capture payload logged by an operator.
 */
@Getter
@Setter
public class MeterReadingRequest {

    @NotNull(message = ValidationMessages.REQUIRED)
    private Long meterId;

    @NotNull(message = ValidationMessages.READING_REQUIRED)
    @DecimalMin(value = "0.0", message = ValidationMessages.POSITIVE_OR_ZERO)
    private BigDecimal currentReading;

    @NotNull(message = ValidationMessages.REQUIRED)
    @Min(value = 1, message = ValidationMessages.INVALID)
    @Max(value = 12, message = ValidationMessages.INVALID)
    private Integer month;

    @NotNull(message = ValidationMessages.REQUIRED)
    @Min(value = 2000, message = ValidationMessages.INVALID)
    private Integer year;
}
