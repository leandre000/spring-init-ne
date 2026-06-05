package com.utility.billing.entity;

import com.utility.billing.common.BaseEntity;
import com.utility.billing.enums.MeterType;
import com.utility.billing.enums.TariffType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "tariffs")
public class Tariff extends BaseEntity {

    @Column(name = "tariff_name", nullable = false, length = 100)
    private String tariffName;

    @Enumerated(EnumType.STRING)
    @Column(name = "meter_type", nullable = false, length = 20)
    private MeterType meterType;

    @Enumerated(EnumType.STRING)
    @Column(name = "tariff_type", nullable = false, length = 20)
    private TariffType tariffType;

    @Column(name = "rate_per_unit", nullable = false, precision = 10, scale = 4)
    private BigDecimal ratePerUnit;

    @Column(name = "fixed_charge", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal fixedCharge = BigDecimal.ZERO;

    @Column(name = "vat_percentage", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal vatPercentage = BigDecimal.ZERO;

    @Column(name = "penalty_percentage", nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal penaltyPercentage = BigDecimal.ZERO;

    @Column(name = "version", nullable = false)
    @Builder.Default
    private Integer version = 1;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";
}
