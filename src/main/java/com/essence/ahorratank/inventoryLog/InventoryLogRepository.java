package com.essence.ahorratank.inventoryLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryLogRepository extends JpaRepository<InventoryLogEntity, Long> {

    // Historial de una estación ordenado por fecha descendente
    List<InventoryLogEntity> findByGasStationIdOrderByCreatedAtDesc(
            Long gasStationId
    );
}