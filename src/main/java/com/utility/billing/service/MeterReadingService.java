package com.utility.billing.service;

import com.utility.billing.dto.MeterReadingDto;
import com.utility.billing.dto.MeterReadingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeterReadingService {
    MeterReadingDto capture(MeterReadingRequest request, String capturedByEmail);
    MeterReadingDto findById(Long id);
    Page<MeterReadingDto> findAll(Long meterId, Integer month, Integer year, Pageable pageable);
}
