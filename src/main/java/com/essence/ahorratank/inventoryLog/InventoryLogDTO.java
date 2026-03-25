package com.essence.ahorratank.inventoryLog;

import com.essence.ahorratank.fuel.FuelType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InventoryLogDTO(
        Long id,
        FuelType fuelType,
        BigDecimal quantityAdded,
        String invoiceNumber,
        String operatorName,
        LocalDateTime createdAt
) {}