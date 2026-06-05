package com.utility.billing.service.impl;

import com.utility.billing.common.filter.BaseSpecification;
import com.utility.billing.common.filter.SearchCriteria;
import com.utility.billing.dto.MeterReadingDto;
import com.utility.billing.dto.MeterReadingRequest;
import com.utility.billing.entity.Meter;
import com.utility.billing.entity.MeterReading;
import com.utility.billing.entity.User;
import com.utility.billing.exception.BusinessException;
import com.utility.billing.exception.DuplicateResourceException;
import com.utility.billing.exception.ResourceNotFoundException;
import com.utility.billing.mapper.MeterReadingMapper;
import com.utility.billing.repository.MeterReadingRepository;
import com.utility.billing.repository.MeterRepository;
import com.utility.billing.repository.UserRepository;
import com.utility.billing.service.MeterReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeterReadingServiceImpl implements MeterReadingService {

    private final MeterReadingRepository meterReadingRepository;
    private final MeterRepository meterRepository;
    private final UserRepository userRepository;
    private final MeterReadingMapper meterReadingMapper;

    @Override
    @Transactional
    public MeterReadingDto capture(MeterReadingRequest request, String capturedByEmail) {
        Meter meter = meterRepository.findById(request.getMeterId())
                .orElseThrow(() -> new ResourceNotFoundException("Meter", "id", request.getMeterId()));

        if (!"ACTIVE".equalsIgnoreCase(meter.getStatus())) {
            throw new BusinessException("Cannot capture reading for an inactive meter", HttpStatus.BAD_REQUEST);
        }

        if (meterReadingRepository.existsByMeterIdAndMonthAndYear(request.getMeterId(), request.getMonth(), request.getYear())) {
            throw new DuplicateResourceException("Reading already exists for this meter in " + request.getMonth() + "/" + request.getYear());
        }

        User user = userRepository.findByEmail(capturedByEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", capturedByEmail));

        BigDecimal previousReading = meterReadingRepository.findFirstByMeterIdOrderByReadingDateDescIdDesc(request.getMeterId())
                .map(MeterReading::getCurrentReading)
                .orElse(BigDecimal.ZERO);

        if (request.getCurrentReading().compareTo(previousReading) <= 0) {
            throw new BusinessException("Current reading must be greater than previous reading: " + previousReading, HttpStatus.BAD_REQUEST);
        }

        BigDecimal consumption = request.getCurrentReading().subtract(previousReading);

        MeterReading reading = MeterReading.builder()
                .meter(meter)
                .previousReading(previousReading)
                .currentReading(request.getCurrentReading())
                .consumption(consumption)
                .month(request.getMonth())
                .year(request.getYear())
                .capturedBy(user)
                .build();

        MeterReading savedReading = meterReadingRepository.save(reading);
        return meterReadingMapper.toDto(savedReading);
    }

    @Override
    public MeterReadingDto findById(Long id) {
        MeterReading reading = meterReadingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MeterReading", "id", id));
        return meterReadingMapper.toDto(reading);
    }

    @Override
    public Page<MeterReadingDto> findAll(Long meterId, Integer month, Integer year, Pageable pageable) {
        List<SearchCriteria> criteria = new ArrayList<>();

        if (meterId != null) {
            criteria.add(new SearchCriteria("meter.id", SearchCriteria.Op.EQUAL, meterId));
        }
        if (month != null) {
            criteria.add(new SearchCriteria("month", SearchCriteria.Op.EQUAL, month));
        }
        if (year != null) {
            criteria.add(new SearchCriteria("year", SearchCriteria.Op.EQUAL, year));
        }

        BaseSpecification<MeterReading> spec = new BaseSpecification<>(criteria);
        Page<MeterReading> page = meterReadingRepository.findAll(spec, pageable);
        return page.map(meterReadingMapper::toDto);
    }
}
