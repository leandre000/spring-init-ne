package com.utility.billing.dto;

import com.utility.billing.common.validation.ValidationMessages;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequest {

    @NotNull(message = ValidationMessages.REQUIRED)
    private Long billId;

    @NotNull(message = ValidationMessages.REQUIRED)
    @DecimalMin(value = "0.01", message = ValidationMessages.POSITIVE)
    private BigDecimal amountPaid;

    @NotBlank(message = ValidationMessages.REQUIRED)
    private String paymentMethod;
}
