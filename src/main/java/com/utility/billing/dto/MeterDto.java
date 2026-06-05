package com.utility.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeterDto {
    private Long id;
    private String meterNumber;
    private String meterType;
    private LocalDate installationDate;
    private String status;
    private Long customerId;
    private String customerName;
}
