package com.utility.billing.repository;

import com.utility.billing.entity.Meter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeterRepository extends JpaRepository<Meter, Long>, JpaSpecificationExecutor<Meter> {
    Optional<Meter> findByMeterNumber(String meterNumber);
    boolean existsByMeterNumber(String meterNumber);
}
