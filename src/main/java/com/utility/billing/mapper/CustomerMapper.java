package com.utility.billing.mapper;

import com.utility.billing.dto.CustomerDto;
import com.utility.billing.dto.CustomerRequest;
import com.utility.billing.entity.Customer;
import com.utility.billing.enums.CustomerStatus;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerDto toDto(Customer customer) {
        if (customer == null) {
            return null;
        }
        return CustomerDto.builder()
                .id(customer.getId())
                .customerCode(customer.getCustomerCode())
                .fullName(customer.getFullName())
                .nationalId(customer.getNationalId())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .address(customer.getAddress())
                .status(customer.getStatus().name())
                .registrationDate(customer.getRegistrationDate())
                .build();
    }

    public Customer toEntity(CustomerRequest request) {
        if (request == null) {
            return null;
        }
        CustomerStatus status = CustomerStatus.ACTIVE;
        if (request.getStatus() != null) {
            try {
                status = CustomerStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Keep default ACTIVE
            }
        }
        return Customer.builder()
                .customerCode(request.getCustomerCode())
                .fullName(request.getFullName())
                .nationalId(request.getNationalId())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .status(status)
                .build();
    }

    public void updateEntity(CustomerRequest request, Customer customer) {
        if (request == null || customer == null) {
            return;
        }
        customer.setCustomerCode(request.getCustomerCode());
        customer.setFullName(request.getFullName());
        customer.setNationalId(request.getNationalId());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setAddress(request.getAddress());
        if (request.getStatus() != null) {
            try {
                customer.setStatus(CustomerStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Keep current status
            }
        }
    }
}
