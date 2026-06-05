package com.utility.billing.service.impl;

import com.utility.billing.common.filter.BaseSpecification;
import com.utility.billing.common.filter.SearchCriteria;
import com.utility.billing.dto.MeterDto;
import com.utility.billing.dto.MeterRequest;
import com.utility.billing.entity.Customer;
import com.utility.billing.entity.Meter;
import com.utility.billing.exception.DuplicateResourceException;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.mapper.MeterMapper;
import com.utility.billing.repository.CustomerRepository;
import com.utility.billing.repository.MeterRepository;
import com.utility.billing.service.MeterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeterServiceImpl implements MeterService {

    private final MeterRepository meterRepository;
    private final CustomerRepository customerRepository;
    private final MeterMapper meterMapper;

    @Override
    @Transactional
    public MeterDto create(MeterRequest request) {
        if (meterRepository.existsByMeterNumber(request.getMeterNumber())) {
            throw new DuplicateResourceException("Meter", "number", request.getMeterNumber());
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        Meter meter = meterMapper.toEntity(request);
        meter.setCustomer(customer);

        Meter savedMeter = meterRepository.save(meter);
        return meterMapper.toDto(savedMeter);
    }

    @Override
    @Transactional
    public MeterDto update(Long id, MeterRequest request) {
        Meter meter = meterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meter", "id", id));

        meterRepository.findByMeterNumber(request.getMeterNumber())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateResourceException("Meter", "number", request.getMeterNumber());
                    }
                });

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        meterMapper.updateEntity(request, meter);
        meter.setCustomer(customer);

        Meter updatedMeter = meterRepository.save(meter);
        return meterMapper.toDto(updatedMeter);
    }

    @Override
    public MeterDto findById(Long id) {
        Meter meter = meterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meter", "id", id));
        return meterMapper.toDto(meter);
    }

    @Override
    public MeterDto findByNumber(String number) {
        Meter meter = meterRepository.findByMeterNumber(number)
                .orElseThrow(() -> new ResourceNotFoundException("Meter", "number", number));
        return meterMapper.toDto(meter);
    }

    @Override
    public Page<MeterDto> findAll(String search, String status, Pageable pageable) {
        List<SearchCriteria> criteria = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            criteria.add(new SearchCriteria("meterNumber", SearchCriteria.Op.LIKE, search));
        }

        if (status != null && !status.isBlank()) {
            criteria.add(new SearchCriteria("status", SearchCriteria.Op.EQUAL, status));
        }

        BaseSpecification<Meter> spec = new BaseSpecification<>(criteria);
        Page<Meter> page = meterRepository.findAll(spec, pageable);
        return page.map(meterMapper::toDto);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Meter meter = meterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meter", "id", id));
        meter.softDelete("system");
        meterRepository.save(meter);
    }
}
