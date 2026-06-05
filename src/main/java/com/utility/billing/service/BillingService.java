package com.utility.billing.service;

import com.utility.billing.dto.BillDto;
import com.utility.billing.dto.BillRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BillingService {
    BillDto generateBill(BillRequest request);
    BillDto findById(Long id);
    BillDto findByNumber(String billNumber);
    Page<BillDto> findAll(Long customerId, String status, Pageable pageable);
    List<BillDto> findMyBills(String email);
    BillDto approveBill(Long id);
}
