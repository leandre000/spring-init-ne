package com.utility.billing.mapper;

import com.utility.billing.dto.MeterDto;
import com.utility.billing.dto.MeterRequest;
import com.utility.billing.entity.Meter;
import com.utility.billing.enums.MeterType;
import org.springframework.stereotype.Component;

@Component
public class MeterMapper {

    public MeterDto toDto(Meter meter) {
        if (meter == null) {
            return null;
        }
        return MeterDto.builder()
                .id(meter.getId())
                .meterNumber(meter.getMeterNumber())
                .meterType(meter.getMeterType().name())
                .installationDate(meter.getInstallationDate())
                .status(meter.getStatus())
                .customerId(meter.getCustomer() != null ? meter.getCustomer().getId() : null)
                .customerName(meter.getCustomer() != null ? meter.getCustomer().getFullName() : null)
                .build();
    }

    public Meter toEntity(MeterRequest request) {
        if (request == null) {
            return null;
        }
        MeterType type = MeterType.valueOf(request.getMeterType().toUpperCase());
        return Meter.builder()
                .meterNumber(request.getMeterNumber())
                .meterType(type)
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .build();
    }

    public void updateEntity(MeterRequest request, Meter meter) {
        if (request == null || meter == null) {
            return;
        }
        meter.setMeterNumber(request.getMeterNumber());
        if (request.getMeterType() != null) {
            meter.setMeterType(MeterType.valueOf(request.getMeterType().toUpperCase()));
        }
        if (request.getStatus() != null) {
            meter.setStatus(request.getStatus());
        }
    }
}
