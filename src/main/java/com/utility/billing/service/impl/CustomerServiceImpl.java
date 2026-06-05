package com.utility.billing.service.impl;

import com.utility.billing.common.filter.BaseSpecification;
import com.utility.billing.common.filter.SearchCriteria;
import com.utility.billing.dto.CustomerDto;
import com.utility.billing.dto.CustomerRequest;
import com.utility.billing.entity.Customer;
import com.utility.billing.enums.CustomerStatus;
import com.utility.billing.exception.DuplicateResourceException;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.mapper.CustomerMapper;
import com.utility.billing.repository.CustomerRepository;
import com.utility.billing.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerDto create(CustomerRequest request) {
        if (customerRepository.existsByCustomerCode(request.getCustomerCode())) {
            throw new DuplicateResourceException("Customer", "code", request.getCustomerCode());
        }
        if (customerRepository.existsByNationalId(request.getNationalId())) {
            throw new DuplicateResourceException("Customer", "nationalId", request.getNationalId());
        }
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Customer", "email", request.getEmail());
        }

        Customer customer = customerMapper.toEntity(request);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toDto(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerDto update(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        customerRepository.findByCustomerCode(request.getCustomerCode())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateResourceException("Customer", "code", request.getCustomerCode());
                    }
                });

        customerRepository.findByNationalId(request.getNationalId())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateResourceException("Customer", "nationalId", request.getNationalId());
                    }
                });

        customerRepository.findByEmail(request.getEmail())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateResourceException("Customer", "email", request.getEmail());
                    }
                });

        customerMapper.updateEntity(request, customer);
        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toDto(updatedCustomer);
    }

    @Override
    public CustomerDto findById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return customerMapper.toDto(customer);
    }

    @Override
    public CustomerDto findByCode(String code) {
        Customer customer = customerRepository.findByCustomerCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "code", code));
        return customerMapper.toDto(customer);
    }

    @Override
    public Page<CustomerDto> findAll(String search, String status, Pageable pageable) {
        List<SearchCriteria> criteria = new ArrayList<>();
        
        if (search != null && !search.isBlank()) {
            criteria.add(new SearchCriteria("fullName", SearchCriteria.Op.LIKE, search));
        }
        
        if (status != null && !status.isBlank()) {
            try {
                CustomerStatus customerStatus = CustomerStatus.valueOf(status.toUpperCase());
                criteria.add(new SearchCriteria("status", SearchCriteria.Op.EQUAL, customerStatus));
            } catch (IllegalArgumentException e) {
                // Ignore invalid status
            }
        }

        BaseSpecification<Customer> spec = new BaseSpecification<>(criteria);
        Page<Customer> page = customerRepository.findAll(spec, pageable);
        return page.map(customerMapper::toDto);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        customer.softDelete("system");
        customerRepository.save(customer);
    }
}
