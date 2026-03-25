package com.essence.ahorratank.inventory;

import com.essence.ahorratank.fuel.FuelType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InventoryDTO(
        FuelType fuelType,
        BigDecimal quantityGallons,
        String status,        // "DISPONIBLE", "BAJO", "SIN STOCK"
        LocalDateTime updatedAt
) {}