package com.essence.ahorratank.gasStation;

import com.essence.ahorratank.fuel.FuelPriceDTO;

import java.util.List;

public record GasStationSummaryDTO(
        Long id,
        String name,
        String address,
        Double latitude,
        Double longitude,
        String zone,
        List<FuelPriceDTO> fuels,
        Double distanceKm
) {}