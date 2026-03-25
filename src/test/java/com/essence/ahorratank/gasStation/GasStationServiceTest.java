package com.essence.ahorratank.gasStation;

import com.essence.ahorratank.fuel.FuelPriceEntity;
import com.essence.ahorratank.fuel.FuelType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GasStationService — pruebas unitarias")
class GasStationServiceTest {

    @Mock GasStationRepository stationRepository;

    @InjectMocks GasStationService stationService;

    // ── fixtures ─────────────────────────────────────────────
    private GasStationEntity stationA;
    private GasStationEntity stationB;

    @BeforeEach
    void setUp() {
        FuelPriceEntity fuelRegular = new FuelPriceEntity();
        fuelRegular.setFuelType(FuelType.REGULAR);
        fuelRegular.setPricePerGallon(new BigDecimal("15100"));
        fuelRegular.setIsAvailable(true);
        fuelRegular.setUpdatedAt(LocalDateTime.now());

        FuelPriceEntity fuelDiesel = new FuelPriceEntity();
        fuelDiesel.setFuelType(FuelType.DIESEL);
        fuelDiesel.setPricePerGallon(new BigDecimal("10980"));
        fuelDiesel.setIsAvailable(true);
        fuelDiesel.setUpdatedAt(LocalDateTime.now());

        stationA = new GasStationEntity();
        stationA.setId(1L);
        stationA.setName("Terpel Autopista Norte");
        stationA.setAddress("Autopista Norte # 153-20, Bogotá");
        stationA.setLatitude(4.7591);
        stationA.setLongitude(-74.0448);
        stationA.setZone("Usaquén");
        stationA.setIsActive(true);
        stationA.setFuels(List.of(fuelRegular, fuelDiesel));

        stationB = new GasStationEntity();
        stationB.setId(2L);
        stationB.setName("Biomax Engativá");
        stationB.setAddress("Calle 80 # 69B-10, Bogotá");
        stationB.setLatitude(4.7112);
        stationB.setLongitude(-74.0891);
        stationB.setZone("Engativá");
        stationB.setIsActive(true);
        stationB.setFuels(List.of(fuelRegular));
    }

    // ── getStations ───────────────────────────────────────────

    @Test
    @DisplayName("getNearby con radio máximo excedido → usa MAX_RADIUS_KM (50)")
    void getNearby_radiusExceedsMax_usesMaxRadius() {
        // Arrange
        double lat = 4.6097, lng = -74.0817;
        StationFilterRequest filter = new StationFilterRequest(
                null, null, null, true, lat, lng, 999
        );
        when(stationRepository.findNearby(eq(lat), eq(lng), eq(50.0), any(), eq(true)))
                .thenReturn(List.of());

        // Act
        stationService.getNearby(filter);

        // Assert — verifica que se llamó con 50.0, no con 999
        verify(stationRepository).findNearby(lat, lng, 50.0, null, true);
    }

    @Test
    @DisplayName("getNearby sin resultados → retorna lista vacía")
    void getNearby_noResults_returnsEmptyList() {
        // Arrange
        StationFilterRequest filter = new StationFilterRequest(
                null, null, null, true, 4.6097, -74.0817, 5
        );
        when(stationRepository.findNearby(anyDouble(), anyDouble(), anyDouble(), any(), eq(true)))
                .thenReturn(List.of());

        // Act
        List<GasStationSummaryDTO> result = stationService.getNearby(filter);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getStations sin filtros → retorna todas las estaciones")
    void getStations_noFilters_returnsAll() {
        // Arrange
        com.essence.ahorratank.gasStation.StationFilterRequest filter = new com.essence.ahorratank.gasStation.StationFilterRequest(
                null, null, null, true, null, null, null
        );
        when(stationRepository.findWithFilters(null, null, null, true))
                .thenReturn(List.of(stationA, stationB));

        // Act
        List<com.essence.ahorratank.gasStation.GasStationSummaryDTO> result = stationService.getStations(filter);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Terpel Autopista Norte");
        assertThat(result.get(1).name()).isEqualTo("Biomax Engativá");
        assertThat(result.get(0).distanceKm()).isNull();
    }

    @Test
    @DisplayName("getStations con filtro de zona → retorna solo esa zona")
    void getStations_withZoneFilter_returnsFiltered() {
        // Arrange
        com.essence.ahorratank.gasStation.StationFilterRequest filter = new com.essence.ahorratank.gasStation.StationFilterRequest(
                "Usaquén", null, null, true, null, null, null
        );
        when(stationRepository.findWithFilters("Usaquén", null, null, true))
                .thenReturn(List.of(stationA));

        // Act
        List<com.essence.ahorratank.gasStation.GasStationSummaryDTO> result = stationService.getStations(filter);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).zone()).isEqualTo("Usaquén");
    }

    @Test
    @DisplayName("getStations sin resultados → retorna lista vacía")
    void getStations_noResults_returnsEmptyList() {
        // Arrange
        com.essence.ahorratank.gasStation.StationFilterRequest filter = new com.essence.ahorratank.gasStation.StationFilterRequest(
                "ZonaInexistente", null, null, true, null, null, null
        );
        when(stationRepository.findWithFilters(any(), any(), any(), anyBoolean()))
                .thenReturn(List.of());

        // Act
        List<com.essence.ahorratank.gasStation.GasStationSummaryDTO> result = stationService.getStations(filter);

        // Assert
        assertThat(result).isEmpty();
    }

    // ── getNearby ─────────────────────────────────────────────

    @Test
    @DisplayName("getNearby → retorna estaciones con distanceKm calculado")
    void getNearby_withLocation_returnsStationsWithDistance() {
        // Arrange — coordenadas del centro de Bogotá
        double lat = 4.6097, lng = -74.0817;
        com.essence.ahorratank.gasStation.StationFilterRequest filter = new com.essence.ahorratank.gasStation.StationFilterRequest(
                null, null, null, true, lat, lng, 10
        );
        when(stationRepository.findNearby(lat, lng, 10, null, true))
                .thenReturn(List.of(stationA, stationB));

        // Act
        List<com.essence.ahorratank.gasStation.GasStationSummaryDTO> result = stationService.getNearby(filter);

        // Assert
        assertThat(result).hasSize(2);
        // distanceKm debe estar calculado (no null)
        assertThat(result.get(0).distanceKm()).isNotNull();
        assertThat(result.get(1).distanceKm()).isNotNull();
        // Las distancias deben ser positivas
        assertThat(result.get(0).distanceKm()).isPositive();
    }

    // ── getDetail ─────────────────────────────────────────────

    @Test
    @DisplayName("getDetail con id válido → retorna detalle completo")
    void getDetail_validId_returnsDetail() {
        // Arrange
        when(stationRepository.findById(1L)).thenReturn(Optional.of(stationA));

        // Act
        com.essence.ahorratank.gasStation.GasStationDetailDTO result = stationService.getDetail(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Terpel Autopista Norte");
        assertThat(result.fuels()).hasSize(2);
        assertThat(result.googleMapsUrl()).contains("4.7591").contains("-74.0448");
    }

    @Test
    @DisplayName("getDetail con id inexistente → ResponseStatusException 404")
    void getDetail_invalidId_throws404() {
        // Arrange
        when(stationRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> stationService.getDetail(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("no encontrada");
    }

    // ── getRouteUrl ───────────────────────────────────────────

    @Test
    @DisplayName("getRouteUrl → genera URL de Google Maps correcta")
    void getRouteUrl_validStation_returnsGoogleMapsUrl() {
        // Arrange
        when(stationRepository.findById(1L)).thenReturn(Optional.of(stationA));

        // Act
        String url = stationService.getRouteUrl(4.6097, -74.0817, 1L);

        // Assert
        assertThat(url)
                .contains("google.com/maps/dir")
                .contains("origin=4.6097,-74.0817")
                .contains("destination=4.7591,-74.0448")
                .contains("travelmode=driving");
    }

    @Test
    @DisplayName("getRouteUrl con estación inexistente → ResponseStatusException 404")
    void getRouteUrl_invalidStation_throws404() {
        // Arrange
        when(stationRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> stationService.getRouteUrl(4.6097, -74.0817, 99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("no encontrada");
    }

    // ── getAvailableZones ─────────────────────────────────────

    @Test
    @DisplayName("getAvailableZones → retorna lista de zonas")
    void getAvailableZones_returnsZones() {
        // Arrange
        when(stationRepository.findDistinctZones())
                .thenReturn(List.of("Bosa", "Chapinero", "Engativá", "Usaquén"));

        // Act
        List<String> zones = stationService.getAvailableZones();

        // Assert
        assertThat(zones).hasSize(4);
        assertThat(zones).containsExactly("Bosa", "Chapinero", "Engativá", "Usaquén");
    }
}