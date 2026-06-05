package com.utility.billing.repository;

import com.utility.billing.entity.Tariff;
import com.utility.billing.enums.MeterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Long>, JpaSpecificationExecutor<Tariff> {
    Optional<Tariff> findFirstByMeterTypeAndStatusOrderByVersionDesc(MeterType meterType, String status);
}
