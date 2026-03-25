package com.essence.ahorratank.inventory;

import com.essence.ahorratank.fuel.FuelType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record RegisterEntryRequest(
        @NotNull(message = "El tipo de combustible es obligatorio")
        FuelType fuelType,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.01", message = "La cantidad debe ser mayor a cero")
        BigDecimal quantityGallons,

        @NotBlank(message = "El número de factura es obligatorio")
        String invoiceNumber
) {}