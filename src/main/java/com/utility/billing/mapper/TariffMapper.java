package com.utility.billing.mapper;

import com.utility.billing.dto.TariffDto;
import com.utility.billing.dto.TariffRequest;
import com.utility.billing.entity.Tariff;
import com.utility.billing.enums.MeterType;
import com.utility.billing.enums.TariffType;
import org.springframework.stereotype.Component;

@Component
public class TariffMapper {

    public TariffDto toDto(Tariff tariff) {
        if (tariff == null) {
            return null;
        }
        return TariffDto.builder()
                .id(tariff.getId())
                .tariffName(tariff.getTariffName())
                .meterType(tariff.getMeterType().name())
                .tariffType(tariff.getTariffType().name())
                .ratePerUnit(tariff.getRatePerUnit())
                .fixedCharge(tariff.getFixedCharge())
                .vatPercentage(tariff.getVatPercentage())
                .penaltyPercentage(tariff.getPenaltyPercentage())
                .version(tariff.getVersion())
                .effectiveFrom(tariff.getEffectiveFrom())
                .effectiveTo(tariff.getEffectiveTo())
                .status(tariff.getStatus())
                .build();
    }

    public Tariff toEntity(TariffRequest request) {
        if (request == null) {
            return null;
        }
        MeterType meterType = MeterType.valueOf(request.getMeterType().toUpperCase());
        TariffType tariffType = TariffType.valueOf(request.getTariffType().toUpperCase());
        return Tariff.builder()
                .tariffName(request.getTariffName())
                .meterType(meterType)
                .tariffType(tariffType)
                .ratePerUnit(request.getRatePerUnit())
                .fixedCharge(request.getFixedCharge())
                .vatPercentage(request.getVatPercentage())
                .penaltyPercentage(request.getPenaltyPercentage())
                .effectiveFrom(request.getEffectiveFrom())
                .effectiveTo(request.getEffectiveTo())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .build();
    }
}
