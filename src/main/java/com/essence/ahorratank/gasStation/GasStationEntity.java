package com.essence.ahorratank.gasStation;

import com.essence.ahorratank.fuel.FuelPriceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// GasStation.java
@Entity
@Table(name = "gas_station")
@Getter
@Setter
@NoArgsConstructor
public class GasStationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false, length = 100)
    private String zone;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "gasStation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FuelPriceEntity> fuels = new ArrayList<>();
}
