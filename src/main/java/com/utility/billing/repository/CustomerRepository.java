package com.utility.billing.repository;

import com.utility.billing.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    Optional<Customer> findByCustomerCode(String customerCode);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByNationalId(String nationalId);
    boolean existsByCustomerCode(String customerCode);
    boolean existsByNationalId(String nationalId);
    boolean existsByEmail(String email);
}
