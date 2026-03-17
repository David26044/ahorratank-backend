package com.essence.ahorratank.gasStation;

import com.essence.ahorratank.fuel.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

// GasStationRepository.java
@Repository
public interface GasStationRepository extends JpaRepository<GasStationEntity, Long> {

    // Filtro por zona
    List<GasStationEntity> findByZoneIgnoreCaseAndIsActiveTrue(String zone);

    // Consulta principal con filtros dinámicos
    @Query("""
        SELECT DISTINCT gs FROM GasStationEntity gs
        JOIN gs.fuels f
        WHERE gs.isActive = true
          AND (:zone     IS NULL OR LOWER(gs.zone) = LOWER(:zone))
          AND (:fuelType IS NULL OR f.fuelType = :fuelType)
          AND (:maxPrice IS NULL OR f.pricePerGallon <= :maxPrice)
          AND (:availableOnly = false OR f.isAvailable = true)
        ORDER BY gs.name ASC
        """)
    List<GasStationEntity> findWithFilters(
            @Param("zone")          String zone,
            @Param("fuelType") FuelType fuelType,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("availableOnly") boolean availableOnly
    );

    // Haversine — estaciones dentro de un radio en km
    @Query(value = """
        SELECT gs.* FROM gas_station gs
        JOIN fuel_price f ON f.gas_station_id = gs.id
        WHERE gs.is_active = true
          AND (:fuelType IS NULL OR f.fuel_type = CAST(:fuelType AS VARCHAR))
          AND (:availableOnly = false OR f.is_available = true)
          AND (
            6371 * ACOS(
              COS(RADIANS(:lat)) * COS(RADIANS(gs.latitude))
              * COS(RADIANS(gs.longitude) - RADIANS(:lng))
              + SIN(RADIANS(:lat)) * SIN(RADIANS(gs.latitude))
            )
          ) <= :radiusKm
        GROUP BY gs.id
        ORDER BY (
            6371 * ACOS(
              COS(RADIANS(:lat)) * COS(RADIANS(gs.latitude))
              * COS(RADIANS(gs.longitude) - RADIANS(:lng))
              + SIN(RADIANS(:lat)) * SIN(RADIANS(gs.latitude))
            )
        ) ASC
        """, nativeQuery = true)
    List<GasStationEntity> findNearby(
            @Param("lat")           double lat,
            @Param("lng")           double lng,
            @Param("radiusKm")      double radiusKm,
            @Param("fuelType")      String fuelType,
            @Param("availableOnly") boolean availableOnly
    );

    // GasStationRepository.java — agregar este método
    @Query("SELECT DISTINCT gs.zone FROM GasStationEntity gs WHERE gs.isActive = true ORDER BY gs.zone ASC")
    List<String> findDistinctZones();
}