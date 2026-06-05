package com.utility.billing.mapper;

import com.utility.billing.dto.MeterReadingDto;
import com.utility.billing.dto.MeterReadingRequest;
import com.utility.billing.entity.MeterReading;
import org.springframework.stereotype.Component;

@Component
public class MeterReadingMapper {

    public MeterReadingDto toDto(MeterReading reading) {
        if (reading == null) {
            return null;
        }
        return MeterReadingDto.builder()
                .id(reading.getId())
                .meterId(reading.getMeter() != null ? reading.getMeter().getId() : null)
                .meterNumber(reading.getMeter() != null ? reading.getMeter().getMeterNumber() : null)
                .previousReading(reading.getPreviousReading())
                .currentReading(reading.getCurrentReading())
                .consumption(reading.getConsumption())
                .readingDate(reading.getReadingDate())
                .month(reading.getMonth())
                .year(reading.getYear())
                .capturedByEmail(reading.getCapturedBy() != null ? reading.getCapturedBy().getEmail() : null)
                .build();
    }

    public MeterReading toEntity(MeterReadingRequest request) {
        if (request == null) {
            return null;
        }
        return MeterReading.builder()
                .currentReading(request.getCurrentReading())
                .month(request.getMonth())
                .year(request.getYear())
                .build();
    }
}
