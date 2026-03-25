package com.essence.ahorratank.inventory;

import com.essence.ahorratank.fuel.FuelType;
import com.essence.ahorratank.gasStation.GasStationEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"gas_station_id", "fuel_type"}))
@Getter @Setter @NoArgsConstructor
public class InventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gas_station_id", nullable = false)
    private GasStationEntity gasStation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FuelType fuelType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantityGallons = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}