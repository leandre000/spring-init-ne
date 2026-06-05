package com.utility.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private Long id;
    private String paymentReference;
    private Long billId;
    private String billNumber;
    private BigDecimal amountPaid;
    private String paymentMethod;
    private Instant paymentDate;
    private String receivedByEmail;
}
