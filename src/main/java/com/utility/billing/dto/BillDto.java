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
public class BillDto {
    private Long id;
    private String billNumber;
    private Long customerId;
    private String customerName;
    private Long meterId;
    private String meterNumber;
    private Long meterReadingId;
    private Long tariffId;
    private String tariffName;
    private Integer billingMonth;
    private Integer billingYear;
    private BigDecimal consumption;
    private BigDecimal amountBeforeTax;
    private BigDecimal taxAmount;
    private BigDecimal penaltyAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balance;
    private String status;
    private Instant generatedDate;
}
