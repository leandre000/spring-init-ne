package com.utility.billing.service.impl;

import com.utility.billing.common.filter.BaseSpecification;
import com.utility.billing.common.filter.SearchCriteria;
import com.utility.billing.dto.PaymentDto;
import com.utility.billing.dto.PaymentRequest;
import com.utility.billing.entity.Bill;
import com.utility.billing.entity.Payment;
import com.utility.billing.entity.User;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.mapper.PaymentMapper;
import com.utility.billing.repository.BillRepository;
import com.utility.billing.repository.PaymentRepository;
import com.utility.billing.repository.UserRepository;
import com.utility.billing.service.PaymentService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public PaymentDto recordPayment(PaymentRequest request, String receivedByEmail) {
        Bill bill = billRepository.findById(request.getBillId())
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "id", request.getBillId()));

        User user = userRepository.findByEmail(receivedByEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", receivedByEmail));

        String reference = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = paymentMapper.toEntity(request);
        payment.setPaymentReference(reference);
        payment.setBill(bill);
        payment.setReceivedBy(user);
        payment.setPaymentDate(Instant.now());

        Payment savedPayment = paymentRepository.save(payment);

        entityManager.flush();
        entityManager.refresh(bill);

        return paymentMapper.toDto(savedPayment);
    }

    @Override
    public PaymentDto findById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return paymentMapper.toDto(payment);
    }

    @Override
    public PaymentDto findByReference(String reference) {
        Payment payment = paymentRepository.findByPaymentReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "reference", reference));
        return paymentMapper.toDto(payment);
    }

    @Override
    public Page<PaymentDto> findAll(Long billId, Pageable pageable) {
        List<SearchCriteria> criteria = new ArrayList<>();
        if (billId != null) {
            criteria.add(new SearchCriteria("bill.id", SearchCriteria.Op.EQUAL, billId));
        }
        BaseSpecification<Payment> spec = new BaseSpecification<>(criteria);
        Page<Payment> page = paymentRepository.findAll(spec, pageable);
        return page.map(paymentMapper::toDto);
    }

    @Override
    public List<PaymentDto> findMyPayments(String email) {
        // Find user by email first
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        
        // Find payments associated with the bills belonging to user's email
        List<Payment> payments = paymentRepository.findByBillCustomerId(user.getId());
        return payments.stream().map(paymentMapper::toDto).toList();
    }
}
