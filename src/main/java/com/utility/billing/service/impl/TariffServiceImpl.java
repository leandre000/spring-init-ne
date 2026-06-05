package com.utility.billing.service.impl;

import com.utility.billing.common.filter.BaseSpecification;
import com.utility.billing.common.filter.SearchCriteria;
import com.utility.billing.dto.TariffDto;
import com.utility.billing.dto.TariffRequest;
import com.utility.billing.entity.Tariff;
import com.utility.billing.enums.MeterType;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.mapper.TariffMapper;
import com.utility.billing.repository.TariffRepository;
import com.utility.billing.service.TariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffServiceImpl implements TariffService {

    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;

    @Override
    @Transactional
    public TariffDto create(TariffRequest request) {
        MeterType meterType = MeterType.valueOf(request.getMeterType().toUpperCase());
        
        int version = 1;
        Tariff previousActive = tariffRepository.findFirstByMeterTypeAndStatusOrderByVersionDesc(meterType, "ACTIVE")
                .orElse(null);

        if (previousActive != null) {
            version = previousActive.getVersion() + 1;
            previousActive.setEffectiveTo(request.getEffectiveFrom().minusDays(1));
            previousActive.setStatus("INACTIVE");
            tariffRepository.save(previousActive);
        }

        Tariff tariff = tariffMapper.toEntity(request);
        tariff.setVersion(version);
        tariff.setStatus("ACTIVE");

        Tariff savedTariff = tariffRepository.save(tariff);
        return tariffMapper.toDto(savedTariff);
    }

    @Override
    @Transactional
    public TariffDto update(Long id, TariffRequest request) {
        Tariff tariff = tariffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tariff", "id", id));

        tariff.setTariffName(request.getTariffName());
        tariff.setRatePerUnit(request.getRatePerUnit());
        tariff.setFixedCharge(request.getFixedCharge());
        tariff.setVatPercentage(request.getVatPercentage());
        tariff.setPenaltyPercentage(request.getPenaltyPercentage());
        tariff.setEffectiveFrom(request.getEffectiveFrom());
        tariff.setEffectiveTo(request.getEffectiveTo());
        if (request.getStatus() != null) {
            tariff.setStatus(request.getStatus());
        }

        Tariff updatedTariff = tariffRepository.save(tariff);
        return tariffMapper.toDto(updatedTariff);
    }

    @Override
    public TariffDto findById(Long id) {
        Tariff tariff = tariffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tariff", "id", id));
        return tariffMapper.toDto(tariff);
    }

    @Override
    public TariffDto findActiveTariff(MeterType meterType) {
        Tariff tariff = tariffRepository.findFirstByMeterTypeAndStatusOrderByVersionDesc(meterType, "ACTIVE")
                .orElseThrow(() -> new ResourceNotFoundException("No active tariff configured for meter type: " + meterType));
        return tariffMapper.toDto(tariff);
    }

    @Override
    public Page<TariffDto> findAll(String search, String status, Pageable pageable) {
        List<SearchCriteria> criteria = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            criteria.add(new SearchCriteria("tariffName", SearchCriteria.Op.LIKE, search));
        }

        if (status != null && !status.isBlank()) {
            criteria.add(new SearchCriteria("status", SearchCriteria.Op.EQUAL, status));
        }

        BaseSpecification<Tariff> spec = new BaseSpecification<>(criteria);
        Page<Tariff> page = tariffRepository.findAll(spec, pageable);
        return page.map(tariffMapper::toDto);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Tariff tariff = tariffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tariff", "id", id));
        tariff.softDelete("system");
        tariffRepository.save(tariff);
    }
}
