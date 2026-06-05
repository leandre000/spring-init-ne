package com.utility.billing.mapper;

import com.utility.billing.dto.BillDto;
import com.utility.billing.entity.Bill;
import org.springframework.stereotype.Component;

@Component
public class BillMapper {

    public BillDto toDto(Bill bill) {
        if (bill == null) {
            return null;
        }
        return BillDto.builder()
                .id(bill.getId())
                .billNumber(bill.getBillNumber())
                .customerId(bill.getCustomer() != null ? bill.getCustomer().getId() : null)
                .customerName(bill.getCustomer() != null ? bill.getCustomer().getFullName() : null)
                .meterId(bill.getMeter() != null ? bill.getMeter().getId() : null)
                .meterNumber(bill.getMeter() != null ? bill.getMeter().getMeterNumber() : null)
                .meterReadingId(bill.getMeterReading() != null ? bill.getMeterReading().getId() : null)
                .tariffId(bill.getTariff() != null ? bill.getTariff().getId() : null)
                .tariffName(bill.getTariff() != null ? bill.getTariff().getTariffName() : null)
                .billingMonth(bill.getBillingMonth())
                .billingYear(bill.getBillingYear())
                .consumption(bill.getConsumption())
                .amountBeforeTax(bill.getAmountBeforeTax())
                .taxAmount(bill.getTaxAmount())
                .penaltyAmount(bill.getPenaltyAmount())
                .totalAmount(bill.getTotalAmount())
                .paidAmount(bill.getPaidAmount())
                .balance(bill.getBalance())
                .status(bill.getStatus().name())
                .generatedDate(bill.getGeneratedDate())
                .build();
    }
}
