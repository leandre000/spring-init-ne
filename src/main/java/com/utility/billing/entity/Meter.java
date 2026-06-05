package com.utility.billing.entity;

import com.utility.billing.common.BaseEntity;
import com.utility.billing.enums.MeterType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "meters")
public class Meter extends BaseEntity {

    @Column(name = "meter_number", nullable = false, unique = true, length = 50)
    private String meterNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "meter_type", nullable = false, length = 20)
    private MeterType meterType;

    @Column(name = "installation_date", nullable = false)
    @Builder.Default
    private LocalDate installationDate = LocalDate.now();

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}
