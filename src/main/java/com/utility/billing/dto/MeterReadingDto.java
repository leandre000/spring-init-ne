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
public class MeterReadingDto {
    private Long id;
    private Long meterId;
    private String meterNumber;
    private BigDecimal previousReading;
    private BigDecimal currentReading;
    private BigDecimal consumption;
    private LocalDate readingDate;
    private Integer month;
    private Integer year;
    private String capturedByEmail;
}
