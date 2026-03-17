package com.essence.ahorratank.fuel;

import com.essence.ahorratank.gasStation.GasStationEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// FuelPrice.java
@Entity
@Table(name = "fuel_price",
        uniqueConstraints = @UniqueConstraint(columnNames = {"gas_station_id", "fuel_type"}))
@Getter
@Setter @NoArgsConstructor
public class FuelPriceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gas_station_id", nullable = false)
    private GasStationEntity gasStation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FuelType fuelType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerGallon;

    @Column(nullable = false)
    private Boolean isAvailable = true;

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
