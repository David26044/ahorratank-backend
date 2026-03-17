package com.essence.ahorratank.gasStation;

import com.essence.ahorratank.fuel.FuelPriceDTO;

import java.util.List;

public record GasStationDetailDTO(
        Long id,
        String name,
        String address,
        Double latitude,
        Double longitude,
        String zone,
        Boolean isActive,
        List<FuelPriceDTO> fuels,
        String googleMapsUrl
) {}