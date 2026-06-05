package com.utility.billing.dto;

import com.utility.billing.common.validation.ValidationMessages;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillRequest {
    @NotNull(message = ValidationMessages.REQUIRED)
    private Long meterReadingId;
}
