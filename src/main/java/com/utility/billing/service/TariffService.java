package com.utility.billing.service;

import com.utility.billing.dto.TariffDto;
import com.utility.billing.dto.TariffRequest;
import com.utility.billing.enums.MeterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TariffService {
    TariffDto create(TariffRequest request);
    TariffDto update(Long id, TariffRequest request);
    TariffDto findById(Long id);
    TariffDto findActiveTariff(MeterType meterType);
    Page<TariffDto> findAll(String search, String status, Pageable pageable);
    void delete(Long id);
}
