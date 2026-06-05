package com.utility.billing.repository;

import com.utility.billing.entity.MeterReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeterReadingRepository extends JpaRepository<MeterReading, Long>, JpaSpecificationExecutor<MeterReading> {
    Optional<MeterReading> findFirstByMeterIdOrderByReadingDateDescIdDesc(Long meterId);
    boolean existsByMeterIdAndMonthAndYear(Long meterId, Integer month, Integer year);
}
