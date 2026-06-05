package com.utility.billing.mapper;

import com.utility.billing.dto.PaymentDto;
import com.utility.billing.dto.PaymentRequest;
import com.utility.billing.entity.Payment;
import com.utility.billing.enums.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentDto toDto(Payment payment) {
        if (payment == null) {
            return null;
        }
        return PaymentDto.builder()
                .id(payment.getId())
                .paymentReference(payment.getPaymentReference())
                .billId(payment.getBill() != null ? payment.getBill().getId() : null)
                .billNumber(payment.getBill() != null ? payment.getBill().getBillNumber() : null)
                .amountPaid(payment.getAmountPaid())
                .paymentMethod(payment.getPaymentMethod().name())
                .paymentDate(payment.getPaymentDate())
                .receivedByEmail(payment.getReceivedBy() != null ? payment.getReceivedBy().getEmail() : null)
                .build();
    }

    public Payment toEntity(PaymentRequest request) {
        if (request == null) {
            return null;
        }
        PaymentMethod method = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        return Payment.builder()
                .amountPaid(request.getAmountPaid())
                .paymentMethod(method)
                .build();
    }
}
