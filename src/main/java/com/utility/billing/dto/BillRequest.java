package com.utility.billing.dto;

import com.utility.billing.common.validation.ValidationMessages;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO representing a request to generate a bill from a meter reading.
 */
@Getter
@Setter
public class BillRequest {
    @NotNull(message = ValidationMessages.REQUIRED)
    private Long meterReadingId;
}
