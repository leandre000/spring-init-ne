package com.utility.billing.entity;

import com.utility.billing.common.BaseEntity;
import com.utility.billing.enums.BillStatus;
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

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bills")
public class Bill extends BaseEntity {

    @Column(name = "bill_number", nullable = false, unique = true, length = 50)
    private String billNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "meter_id", nullable = false)
    private Meter meter;

    @ManyToOne
    @JoinColumn(name = "meter_reading_id", nullable = false)
    private MeterReading meterReading;

    @ManyToOne
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @Column(name = "billing_month", nullable = false)
    private Integer billingMonth;

    @Column(name = "billing_year", nullable = false)
    private Integer billingYear;

    @Column(name = "consumption", nullable = false, precision = 12, scale = 2)
    private BigDecimal consumption;

    @Column(name = "amount_before_tax", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountBeforeTax;

    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "penalty_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal penaltyAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "paid_amount", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private BillStatus status = BillStatus.PENDING;

    @Column(name = "generated_date", nullable = false)
    @Builder.Default
    private Instant generatedDate = Instant.now();
}
