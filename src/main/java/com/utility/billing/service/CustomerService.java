package com.utility.billing.service;

import com.utility.billing.dto.CustomerDto;
import com.utility.billing.dto.CustomerRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    CustomerDto create(CustomerRequest request);
    CustomerDto update(Long id, CustomerRequest request);
    CustomerDto findById(Long id);
    CustomerDto findByCode(String code);
    Page<CustomerDto> findAll(String search, String status, Pageable pageable);
    void delete(Long id);
}
