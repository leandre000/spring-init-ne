package com.utility.billing.service.impl;

import com.utility.billing.common.filter.BaseSpecification;
import com.utility.billing.common.filter.SearchCriteria;
import com.utility.billing.dto.BillDto;
import com.utility.billing.dto.BillRequest;
import com.utility.billing.entity.Bill;
import com.utility.billing.entity.Customer;
import com.utility.billing.entity.MeterReading;
import com.utility.billing.entity.Tariff;
import com.utility.billing.enums.BillStatus;
import com.utility.billing.enums.CustomerStatus;
import com.utility.billing.enums.MeterType;
import com.utility.billing.exception.BusinessException;
import com.utility.billing.exception.DuplicateResourceException;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.mapper.BillMapper;
import com.utility.billing.repository.BillRepository;
import com.utility.billing.repository.CustomerRepository;
import com.utility.billing.repository.MeterReadingRepository;
import com.utility.billing.repository.TariffRepository;
import com.utility.billing.service.BillingService;
import com.utility.billing.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation handling utility bill computations, threshold rate matching, and invoice dispatching.
 */
@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

    private final BillRepository billRepository;
    private final MeterReadingRepository meterReadingRepository;
    private final TariffRepository tariffRepository;
    private final CustomerRepository customerRepository;
    private final BillMapper billMapper;
    private final EmailService emailService;

    @Override
    @Transactional
    public BillDto generateBill(BillRequest request) {
        if (billRepository.existsByMeterReadingId(request.getMeterReadingId())) {
            throw new DuplicateResourceException("Bill already generated for meter reading: " + request.getMeterReadingId());
        }

        MeterReading reading = meterReadingRepository.findById(request.getMeterReadingId())
                .orElseThrow(() -> new ResourceNotFoundException("MeterReading", "id", request.getMeterReadingId()));

        Customer customer = reading.getMeter().getCustomer();
        if (customer.getStatus() == CustomerStatus.INACTIVE) {
            throw new BusinessException("Cannot generate bills for inactive customers", HttpStatus.BAD_REQUEST);
        }

        MeterType meterType = reading.getMeter().getMeterType();
        Tariff tariff = tariffRepository.findFirstByMeterTypeAndStatusOrderByVersionDesc(meterType, "ACTIVE")
                .orElseThrow(() -> new ResourceNotFoundException("No active tariff configured for meter type: " + meterType));

        BigDecimal rate = tariff.getRatePerUnit();
        BigDecimal consumption = reading.getConsumption();
        BigDecimal consumptionCost = consumption.multiply(rate);
        BigDecimal amountBeforeTax = consumptionCost.add(tariff.getFixedCharge());
        
        BigDecimal vatDivisor = tariff.getVatPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal taxAmount = amountBeforeTax.multiply(vatDivisor);
        
        BigDecimal penaltyDivisor = tariff.getPenaltyPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal penaltyAmount = amountBeforeTax.multiply(penaltyDivisor);
        
        BigDecimal totalAmount = amountBeforeTax.add(taxAmount).add(penaltyAmount);
        
        amountBeforeTax = amountBeforeTax.setScale(2, RoundingMode.HALF_UP);
        taxAmount = taxAmount.setScale(2, RoundingMode.HALF_UP);
        penaltyAmount = penaltyAmount.setScale(2, RoundingMode.HALF_UP);
        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);

        String billNumber = "BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Bill bill = Bill.builder()
                .billNumber(billNumber)
                .customer(customer)
                .meter(reading.getMeter())
                .meterReading(reading)
                .tariff(tariff)
                .billingMonth(reading.getMonth())
                .billingYear(reading.getYear())
                .consumption(reading.getConsumption())
                .amountBeforeTax(amountBeforeTax)
                .taxAmount(taxAmount)
                .penaltyAmount(penaltyAmount)
                .totalAmount(totalAmount)
                .paidAmount(BigDecimal.ZERO)
                .balance(totalAmount)
                .status(BillStatus.PENDING)
                .generatedDate(Instant.now())
                .build();

        Bill savedBill = billRepository.save(bill);

        // Send email alert to customer
        emailService.sendBillNotificationEmail(customer, savedBill);

        return billMapper.toDto(savedBill);
    }

    @Override
    public BillDto findById(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "id", id));
        return billMapper.toDto(bill);
    }

    @Override
    public BillDto findByNumber(String billNumber) {
        Bill bill = billRepository.findByBillNumber(billNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "billNumber", billNumber));
        return billMapper.toDto(bill);
    }

    @Override
    public Page<BillDto> findAll(Long customerId, String status, Pageable pageable) {
        List<SearchCriteria> criteria = new ArrayList<>();
        if (customerId != null) {
            criteria.add(new SearchCriteria("customer.id", SearchCriteria.Op.EQUAL, customerId));
        }
        if (status != null && !status.isBlank()) {
            try {
                BillStatus billStatus = BillStatus.valueOf(status.toUpperCase());
                criteria.add(new SearchCriteria("status", SearchCriteria.Op.EQUAL, billStatus));
            } catch (IllegalArgumentException e) {
                // Ignore invalid status
            }
        }
        BaseSpecification<Bill> spec = new BaseSpecification<>(criteria);
        Page<Bill> page = billRepository.findAll(spec, pageable);
        return page.map(billMapper::toDto);
    }

    @Override
    public List<BillDto> findMyBills(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer account not associated with email: " + email));
        List<Bill> bills = billRepository.findByCustomerId(customer.getId());
        return bills.stream().map(billMapper::toDto).toList();
    }

    @Override
    @Transactional
    public BillDto approveBill(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill", "id", id));
        return billMapper.toDto(bill);
    }
}
