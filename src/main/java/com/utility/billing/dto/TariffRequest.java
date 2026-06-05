package com.utility.billing.dto;

import com.utility.billing.common.validation.ValidationMessages;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TariffRequest {

    @NotBlank(message = ValidationMessages.REQUIRED)
    private String tariffName;

    @NotBlank(message = ValidationMessages.REQUIRED)
    private String meterType;

    @NotBlank(message = ValidationMessages.REQUIRED)
    private String tariffType;

    @NotNull(message = ValidationMessages.REQUIRED)
    @DecimalMin(value = "0.0", message = ValidationMessages.POSITIVE_OR_ZERO)
    private BigDecimal ratePerUnit;

    @NotNull(message = ValidationMessages.REQUIRED)
    @DecimalMin(value = "0.0", message = ValidationMessages.POSITIVE_OR_ZERO)
    private BigDecimal fixedCharge;

    @NotNull(message = ValidationMessages.REQUIRED)
    @DecimalMin(value = "0.0", message = ValidationMessages.POSITIVE_OR_ZERO)
    private BigDecimal vatPercentage;

    @NotNull(message = ValidationMessages.REQUIRED)
    @DecimalMin(value = "0.0", message = ValidationMessages.POSITIVE_OR_ZERO)
    private BigDecimal penaltyPercentage;

    @NotNull(message = ValidationMessages.REQUIRED)
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    private String status;
}
