package com.utility.billing.entity;

import com.utility.billing.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "meter_readings")
public class MeterReading extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "meter_id", nullable = false)
    private Meter meter;

    @Column(name = "previous_reading", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal previousReading = BigDecimal.ZERO;

    @Column(name = "current_reading", nullable = false, precision = 12, scale = 2)
    private BigDecimal currentReading;

    @Column(name = "consumption", nullable = false, precision = 12, scale = 2)
    private BigDecimal consumption;

    @Column(name = "reading_date", nullable = false)
    @Builder.Default
    private LocalDate readingDate = LocalDate.now();

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "year", nullable = false)
    private Integer year;

    @ManyToOne
    @JoinColumn(name = "captured_by", nullable = false)
    private User capturedBy;
}
