package com.essence.ahorratank.fuel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// FuelPriceDTO.java
public record FuelPriceDTO(
        FuelType fuelType,
        BigDecimal pricePerGallon,
        Boolean isAvailable,
        LocalDateTime updatedAt
) {}
