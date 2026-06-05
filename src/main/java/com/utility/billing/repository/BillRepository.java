package com.utility.billing.repository;

import com.utility.billing.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long>, JpaSpecificationExecutor<Bill> {
    Optional<Bill> findByBillNumber(String billNumber);
    boolean existsByBillNumber(String billNumber);
    boolean existsByMeterReadingId(Long meterReadingId);
    List<Bill> findByCustomerId(Long customerId);
}
