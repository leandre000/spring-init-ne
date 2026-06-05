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
public class CustomerDto {
    private Long id;
    private String customerCode;
    private String fullName;
    private String nationalId;
    private String email;
    private String phoneNumber;
    private String address;
    private String status;
    private LocalDate registrationDate;
}
