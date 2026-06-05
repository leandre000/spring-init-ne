package com.utility.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TariffDto {
    private Long id;
    private String tariffName;
    private String meterType;
    private String tariffType;
    private BigDecimal ratePerUnit;
    private BigDecimal fixedCharge;
    private BigDecimal vatPercentage;
    private BigDecimal penaltyPercentage;
    private Integer version;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private String status;
}
