package com.utility.billing.service;

import com.utility.billing.dto.PaymentDto;
import com.utility.billing.dto.PaymentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {
    PaymentDto recordPayment(PaymentRequest request, String receivedByEmail);
    PaymentDto findById(Long id);
    PaymentDto findByReference(String reference);
    Page<PaymentDto> findAll(Long billId, Pageable pageable);
    List<PaymentDto> findMyPayments(String email);
}
