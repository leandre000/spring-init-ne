package com.utility.billing.dto;

import com.utility.billing.common.validation.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeterRequest {

    @NotBlank(message = ValidationMessages.METER_NUMBER_REQUIRED)
    @Size(max = 50, message = ValidationMessages.TOO_LONG)
    private String meterNumber;

    @NotBlank(message = ValidationMessages.METER_TYPE_REQUIRED)
    private String meterType;

    private String status;

    @NotNull(message = ValidationMessages.REQUIRED)
    private Long customerId;
}
