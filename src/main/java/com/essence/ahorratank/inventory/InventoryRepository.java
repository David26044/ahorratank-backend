package com.essence.ahorratank.inventory;

import com.essence.ahorratank.fuel.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {

    // Inventario completo de una estación
    List<InventoryEntity> findByGasStationId(Long gasStationId);

    // Un combustible específico de una estación
    Optional<InventoryEntity> findByGasStationIdAndFuelType(
            Long gasStationId, FuelType fuelType
    );

    List<InventoryEntity> findAllByGasStationIdAndFuelType(
            Long gasStationId, FuelType fuelType
    );
}