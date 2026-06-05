package com.utility.billing.service;

import com.utility.billing.dto.MeterDto;
import com.utility.billing.dto.MeterRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MeterService {
    MeterDto create(MeterRequest request);
    MeterDto update(Long id, MeterRequest request);
    MeterDto findById(Long id);
    MeterDto findByNumber(String number);
    Page<MeterDto> findAll(String search, String status, Pageable pageable);
    void delete(Long id);
}
