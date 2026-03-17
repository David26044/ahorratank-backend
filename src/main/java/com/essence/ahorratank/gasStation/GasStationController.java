package com.essence.ahorratank.gasStation;

import com.essence.ahorratank.fuel.FuelType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

// GasStationController.java
@RestController
@RequestMapping("/stations")
@RequiredArgsConstructor
public class GasStationController {

    private final GasStationService stationService;

    /**
     * UC-005 / UC-007 — Listado con filtros opcionales
     * GET /api/v1/stations?zone=Centro&fuelType=REGULAR&maxPrice=15000&availableOnly=true
     */
    @GetMapping
    public ResponseEntity<List<GasStationSummaryDTO>> getStations(
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) FuelType fuelType,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean availableOnly) {

        StationFilterRequest filter = new StationFilterRequest(
                zone, fuelType, maxPrice, availableOnly, null, null, null
        );

        return ResponseEntity.ok(stationService.getStations(filter));
    }

    /**
     * UC-006 — Estaciones cercanas
     * GET /api/v1/stations/nearby?lat=4.6097&lng=-74.0817&radiusKm=5&fuelType=REGULAR
     */
    @GetMapping("/nearby")
    public ResponseEntity<List<GasStationSummaryDTO>> getNearby(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false) Integer radiusKm,
            @RequestParam(required = false) FuelType fuelType,
            @RequestParam(required = false) Boolean availableOnly) {

        StationFilterRequest filter = new StationFilterRequest(
                null, fuelType, null, availableOnly, lat, lng, radiusKm
        );

        return ResponseEntity.ok(stationService.getNearby(filter));
    }

    /**
     * UC-008 — Detalle de una estación
     * GET /api/v1/stations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<GasStationDetailDTO> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(stationService.getDetail(id));
    }

    /**
     * UC-009 — Generar enlace de ruta
     * GET /api/v1/stations/{id}/route?originLat=4.6097&originLng=-74.0817
     */
    @GetMapping("/{id}/route")
    public ResponseEntity<Map<String, String>> getRoute(
            @PathVariable Long id,
            @RequestParam Double originLat,
            @RequestParam Double originLng) {

        String url = stationService.getRouteUrl(originLat, originLng, id);
        return ResponseEntity.ok(Map.of("routeUrl", url));
    }

    /**
     * UC-004 — Zonas disponibles para el dropdown
     * GET /api/v1/stations/zones
     */
    @GetMapping("/zones")
    public ResponseEntity<List<String>> getZones() {
        return ResponseEntity.ok(stationService.getAvailableZones());
    }
}