package com.essence.ahorratank.inventoryLog;

import com.essence.ahorratank.fuel.FuelType;
import com.essence.ahorratank.gasStation.GasStationEntity;
import com.essence.ahorratank.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_log")
@Getter @Setter @NoArgsConstructor
public class InventoryLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gas_station_id", nullable = false)
    private GasStationEntity gasStation;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "operator_id", nullable = false)
    private UserEntity operator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FuelType fuelType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantityAdded;

    @Column(nullable = false, length = 100)
    private String invoiceNumber;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}