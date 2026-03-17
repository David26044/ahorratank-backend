package com.essence.ahorratank.gasStation;

import com.essence.ahorratank.fuel.FuelType;

import java.math.BigDecimal;

public record StationFilterRequest(
        String zone,
        FuelType fuelType,
        BigDecimal maxPrice,
        Boolean availableOnly,
        Double userLat,
        Double userLng,
        Integer nearbyRadiusKm
) {
    // Constructor compacto con defaults
    public StationFilterRequest {
        if (availableOnly == null) availableOnly = true;
        if (nearbyRadiusKm == null) nearbyRadiusKm = 5;
    }
}
