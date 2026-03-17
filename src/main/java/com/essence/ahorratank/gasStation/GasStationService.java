package com.essence.ahorratank.gasStation;

import com.essence.ahorratank.fuel.FuelPriceDTO;
import com.essence.ahorratank.fuel.FuelPriceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// GasStationService.java
@Service
@RequiredArgsConstructor
public class GasStationService {

    private final GasStationRepository stationRepository;

    private static final int DEFAULT_RADIUS_KM = 5;
    private static final int MAX_RADIUS_KM     = 50;

    // UC-005 / UC-007 — listado con filtros
    public List<GasStationSummaryDTO> getStations(StationFilterRequest filter) {
        List<GasStationEntity> stations = stationRepository.findWithFilters(
                filter.zone(),
                filter.fuelType(),
                filter.maxPrice(),
                Boolean.TRUE.equals(filter.availableOnly())
        );
        return stations.stream()
                .map(s -> toSummary(s, null))
                .toList();
    }

    // UC-006 — gasolineras cercanas
    public List<GasStationSummaryDTO> getNearby(StationFilterRequest filter) {
        double lat    = filter.userLat();
        double lng    = filter.userLng();
        int radius    = Math.min(
                filter.nearbyRadiusKm() != null ? filter.nearbyRadiusKm() : DEFAULT_RADIUS_KM,
                MAX_RADIUS_KM
        );
        String fuelParam = filter.fuelType() != null
                ? filter.fuelType().name()
                : null;

        return stationRepository
                .findNearby(lat, lng, radius, fuelParam, Boolean.TRUE.equals(filter.availableOnly()))
                .stream()
                .map(s -> toSummary(s, calculateDistance(lat, lng, s.getLatitude(), s.getLongitude())))
                .toList();
    }

    // UC-008 — detalle de estación
    public GasStationDetailDTO getDetail(Long id) {
        GasStationEntity station = stationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estación no encontrada"));
        return toDetail(station);
    }

    // UC-009 — genera enlace Google Maps (ruta)
    public String getRouteUrl(Double originLat, Double originLng, Long stationId) {
        GasStationEntity dest = stationRepository.findById(stationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estación no encontrada"));

        return String.format(
                "https://www.google.com/maps/dir/?api=1&origin=%s,%s&destination=%s,%s&travelmode=driving",
                originLat, originLng,
                dest.getLatitude(), dest.getLongitude()
        );
    }

    // UC-004 — zonas disponibles para el dropdown
    public List<String> getAvailableZones() {
        return stationRepository.findDistinctZones();
    }

    // ── mappers privados ──────────────────────────────────────

    private GasStationSummaryDTO toSummary(GasStationEntity s, Double distanceKm) {
        return new GasStationSummaryDTO(
                s.getId(),
                s.getName(),
                s.getAddress(),
                s.getLatitude(),
                s.getLongitude(),
                s.getZone(),
                mapFuels(s.getFuels()),
                distanceKm
        );
    }

    private GasStationDetailDTO toDetail(GasStationEntity s) {
        String mapsUrl = String.format(
                "https://www.google.com/maps/search/?api=1&query=%s,%s",
                s.getLatitude(), s.getLongitude()
        );
        return new GasStationDetailDTO(
                s.getId(),
                s.getName(),
                s.getAddress(),
                s.getLatitude(),
                s.getLongitude(),
                s.getZone(),
                s.getIsActive(),
                mapFuels(s.getFuels()),
                mapsUrl
        );
    }

    private List<FuelPriceDTO> mapFuels(List<FuelPriceEntity> fuels) {
        return fuels.stream()
                .map(f -> new FuelPriceDTO(
                        f.getFuelType(),
                        f.getPricePerGallon(),
                        f.getIsAvailable(),
                        f.getUpdatedAt()
                ))
                .toList();
    }

    // Fórmula Haversine en Java
    private double calculateDistance(double lat1, double lng1,
                                     double lat2, double lng2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
