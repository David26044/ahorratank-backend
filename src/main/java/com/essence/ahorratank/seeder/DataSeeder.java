package com.essence.ahorratank.seeder;

import com.essence.ahorratank.fuel.FuelPriceEntity;
import com.essence.ahorratank.gasStation.GasStationEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.essence.ahorratank.fuel.FuelType.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements ApplicationRunner {

    private final com.essence.ahorratank.gasStation.GasStationRepository stationRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (stationRepository.count() > 0) {
            log.info("DataSeeder: datos existentes, omitiendo seed.");
            return;
        }
        log.info("DataSeeder: insertando estaciones...");
        stationRepository.saveAll(buildStations());
        log.info("DataSeeder: {} estaciones insertadas.", stationRepository.count());
    }

    private List<GasStationEntity> buildStations() {
        return List.of(

            // ── USAQUÉN ───────────────────────────────────────────────────────────
            station("Terpel Autopista Norte",
                    "Autopista Norte Cra 45 # 153-20, Bogotá",
                    4.759100, -74.044800, "Usaquén",
                    fuel(REGULAR, "15080"), fuel(PREMIUM, "17350"), fuel(DIESEL, "11060"), fuel(GAS, "3100")),

            station("Primax Cedritos",
                    "Calle 140 # 13-50, Bogotá",
                    4.728600, -74.031200, "Usaquén",
                    fuel(REGULAR, "15150"), fuel(PREMIUM, "17420"), fuel(DIESEL, "10980")),

            station("Biomax Country",
                    "Carrera 15 # 118-30, Bogotá",
                    4.701200, -74.033100, "Usaquén",
                    fuel(REGULAR, "15200"), fuel(PREMIUM, "17500"), fuel(DIESEL, "11010")),

            station("EDS Shell Usaquén",
                    "Calle 116 # 6-20, Bogotá",
                    4.694500, -74.030500, "Usaquén",
                    fuel(REGULAR, "15250"), fuel(PREMIUM, "17540"), fuel(DIESEL, "11020")),

            // ── SUBA ──────────────────────────────────────────────────────────────
            station("Terpel Suba",
                    "Carrera 91 # 145-12, Bogotá",
                    4.741800, -74.092300, "Suba",
                    fuel(REGULAR, "15090"), fuel(PREMIUM, "17360"), fuel(DIESEL, "10970")),

            station("Primax Rincon de Suba",
                    "Calle 128B # 91-55, Bogotá",
                    4.730500, -74.101400, "Suba",
                    fuel(REGULAR, "15120"), fuel(DIESEL, "10950"), fuel(GAS, "3180")),

            station("Biomax Suba Av Boyacá",
                    "Avenida Boyacá # 136-20, Bogotá",
                    4.745600, -74.078900, "Suba",
                    fuel(REGULAR, "15070"), fuel(PREMIUM, "17330"), fuel(DIESEL, "11000")),

            station("EDS Texaco Suba",
                    "Carrera 103 # 149-10, Bogotá",
                    4.756200, -74.097800, "Suba",
                    fuel(REGULAR, "15130"), fuel(PREMIUM, "17400"), fuel(DIESEL, "10990"), fuel(GAS, "3210")),

            // ── ENGATIVÁ ──────────────────────────────────────────────────────────
            station("Biomax Engativá",
                    "Calle 80 # 69B-10, Bogotá",
                    4.711200, -74.089100, "Engativá",
                    fuel(REGULAR, "15060"), fuel(PREMIUM, "17310"), fuel(DIESEL, "10920")),

            station("Terpel Calle 80",
                    "Calle 80 # 95-20, Bogotá",
                    4.712500, -74.110300, "Engativá",
                    fuel(REGULAR, "15100"), fuel(DIESEL, "10960"), fuel(GAS, "3150")),

            station("Primax Morato",
                    "Carrera 53A # 116-35, Bogotá",
                    4.698400, -74.072500, "Engativá",
                    fuel(REGULAR, "15180"), fuel(PREMIUM, "17450"), fuel(DIESEL, "11030")),

            station("EDS Gulf Engativá",
                    "Avenida Boyacá # 63A-12, Bogotá",
                    4.679300, -74.102400, "Engativá",
                    fuel(REGULAR, "15090"), fuel(DIESEL, "10945")),

            // ── BARRIOS UNIDOS / CHAPINERO ────────────────────────────────────────
            station("Terpel Av. Caracas",
                    "Avenida Caracas # 72-30, Bogotá",
                    4.656700, -74.065200, "Barrios Unidos",
                    fuel(REGULAR, "15220"), fuel(PREMIUM, "17480"), fuel(DIESEL, "10970")),

            station("Petrobras Barrios Unidos",
                    "Carrera 24 # 57-10, Bogotá",
                    4.643200, -74.072100, "Barrios Unidos",
                    fuel(REGULAR, "15131"), fuel(DIESEL, "11020")),

            station("Shell Santa Sofía",
                    "Calle 53 # 13-45, Bogotá",
                    4.640100, -74.062300, "Chapinero",
                    fuel(REGULAR, "15260"), fuel(PREMIUM, "17550"), fuel(DIESEL, "11040")),

            station("Biomax Chapinero",
                    "Carrera 13 # 49-20, Bogotá",
                    4.635800, -74.064900, "Chapinero",
                    fuel(REGULAR, "15190"), fuel(PREMIUM, "17460"), fuel(DIESEL, "11010")),

            station("Primax Zona Rosa",
                    "Carrera 13 # 82-55, Bogotá",
                    4.666300, -74.053100, "Chapinero",
                    fuel(REGULAR, "15300"), fuel(PREMIUM, "17600"), fuel(DIESEL, "11080")),

            // ── TEUSAQUILLO ───────────────────────────────────────────────────────
            station("Terpel Calle 68",
                    "Avenida Calle 68 # 28-15, Bogotá",
                    4.661680, -74.073560, "Teusaquillo",
                    fuel(REGULAR, "15089"), fuel(PREMIUM, "17321"), fuel(DIESEL, "11029"), fuel(GAS, "3215")),

            station("EDS Esso Teusaquillo",
                    "Carrera 30 # 45-60, Bogotá",
                    4.629800, -74.087400, "Teusaquillo",
                    fuel(REGULAR, "15140"), fuel(PREMIUM, "17390"), fuel(DIESEL, "10980")),

            station("Biomax NQS",
                    "Avenida NQS Cra 30 # 53-20, Bogotá",
                    4.637500, -74.085100, "Teusaquillo",
                    fuel(REGULAR, "15110"), fuel(DIESEL, "10960"), fuel(GAS, "3170")),

            // ── SANTA FE / LA CANDELARIA ──────────────────────────────────────────
            station("Biomax Centro",
                    "Calle 13 # 10-22, Bogotá",
                    4.598100, -74.076200, "Santa Fe",
                    fuel(REGULAR, "15310"), fuel(PREMIUM, "17590"), fuel(DIESEL, "11090")),

            station("Terpel Av. Jiménez",
                    "Avenida Jiménez # 14-50, Bogotá",
                    4.600400, -74.072100, "Santa Fe",
                    fuel(REGULAR, "15280"), fuel(PREMIUM, "17560"), fuel(DIESEL, "11070")),

            station("Primax San Victorino",
                    "Calle 11 # 15-30, Bogotá",
                    4.596200, -74.079800, "Santa Fe",
                    fuel(REGULAR, "15240"), fuel(DIESEL, "11050")),

            // ── PUENTE ARANDA / FONTIBÓN ──────────────────────────────────────────
            station("Terpel Av. Américas",
                    "Avenida de las Américas # 53-21, Bogotá",
                    4.628500, -74.102100, "Puente Aranda",
                    fuel(REGULAR, "15100"), fuel(PREMIUM, "17370"), fuel(DIESEL, "10950"), fuel(GAS, "3140")),

            station("EDS Esso Puente Aranda",
                    "Carrera 50 # 9-30, Bogotá",
                    4.617300, -74.104600, "Puente Aranda",
                    fuel(REGULAR, "15050"), fuel(DIESEL, "10930")),

            station("Biomax Fontibón",
                    "Carrera 99 # 22A-30, Bogotá",
                    4.671200, -74.143100, "Fontibón",
                    fuel(REGULAR, "15080"), fuel(PREMIUM, "17340"), fuel(DIESEL, "10960")),

            station("Primax Aeropuerto",
                    "Calle 26 # 103-50, Bogotá",
                    4.663400, -74.151200, "Fontibón",
                    fuel(REGULAR, "15070"), fuel(PREMIUM, "17320"), fuel(DIESEL, "10940"), fuel(GAS, "3120")),

            station("Terpel Fontibón",
                    "Carrera 112 # 17-20, Bogotá",
                    4.658900, -74.158300, "Fontibón",
                    fuel(REGULAR, "15060"), fuel(DIESEL, "10920")),

            // ── KENNEDY ───────────────────────────────────────────────────────────
            station("Biomax Kennedy",
                    "Calle 38 Sur # 78-10, Bogotá",
                    4.624100, -74.150200, "Kennedy",
                    fuel(REGULAR, "15040"), fuel(DIESEL, "10900")),

            station("Terpel Kennedy Calle 13",
                    "Calle 13 # 78-35, Bogotá",
                    4.637200, -74.145800, "Kennedy",
                    fuel(REGULAR, "15060"), fuel(PREMIUM, "17310"), fuel(DIESEL, "10930"), fuel(GAS, "3090")),

            station("Primax Tintal",
                    "Avenida Calle 6 # 106-20, Bogotá",
                    4.620500, -74.158700, "Kennedy",
                    fuel(REGULAR, "15030"), fuel(DIESEL, "10890")),

            station("EDS Texaco Kennedy",
                    "Carrera 86 # 42-15 Sur, Bogotá",
                    4.610800, -74.162300, "Kennedy",
                    fuel(REGULAR, "15050"), fuel(PREMIUM, "17300"), fuel(DIESEL, "10910")),

            station("Vanti GNV Kennedy",
                    "Avenida Caracas # 38-20 Sur, Bogotá",
                    4.573100, -74.124500, "Kennedy",
                    fuel(REGULAR, "15235"), fuel(DIESEL, "11046"), fuel(PREMIUM, "17490"), fuel(GAS, "3250")),

            // ── BOSA ──────────────────────────────────────────────────────────────
            station("Biomax Bosa",
                    "Carrera 80 # 57-20 Sur, Bogotá",
                    4.592100, -74.177200, "Bosa",
                    fuel(REGULAR, "15020"), fuel(DIESEL, "10880")),

            station("Terpel Bosa Central",
                    "Calle 65 Sur # 78D-30, Bogotá",
                    4.580600, -74.181400, "Bosa",
                    fuel(REGULAR, "15010"), fuel(DIESEL, "10870"), fuel(GAS, "3080")),

            station("Primax Bosa El Porvenir",
                    "Carrera 97B # 73-12 Sur, Bogotá",
                    4.575300, -74.192600, "Bosa",
                    fuel(REGULAR, "15000"), fuel(DIESEL, "10860")),

            // ── RAFAEL URIBE / ANTONIO NARIÑO ─────────────────────────────────────
            station("Antigua Gasolinera Sur",
                    "Calle 22 Sur # 14-40, Bogotá",
                    4.589040, -74.080670, "Antonio Nariño",
                    fuel(REGULAR, "15236"), fuel(PREMIUM, "17489"), fuel(DIESEL, "10888"), fuel(GAS, "3298")),

            station("EDS Shell Sur",
                    "Carrera 10 # 27-30 Sur, Bogotá",
                    4.578200, -74.082100, "Rafael Uribe",
                    fuel(REGULAR, "15100"), fuel(DIESEL, "10940")),

            station("Biomax Quiroga",
                    "Avenida Caracas # 34-20 Sur, Bogotá",
                    4.584900, -74.070300, "Rafael Uribe",
                    fuel(REGULAR, "15120"), fuel(PREMIUM, "17370"), fuel(DIESEL, "10950")),

            // ── CIUDAD BOLÍVAR / USME ─────────────────────────────────────────────
            station("Terpel Usme",
                    "Carrera 5 Este # 84-12 Sur, Bogotá",
                    4.527100, -74.082100, "Usme",
                    fuel(REGULAR, "15000"), fuel(DIESEL, "10880")),

            station("Primax Ciudad Bolívar",
                    "Avenida Boyacá # 72-30 Sur, Bogotá",
                    4.543200, -74.135600, "Ciudad Bolívar",
                    fuel(REGULAR, "14990"), fuel(DIESEL, "10870")),

            station("Biomax Autopista Sur",
                    "Autopista Sur # 48-20, Bogotá",
                    4.556800, -74.148200, "Ciudad Bolívar",
                    fuel(REGULAR, "15010"), fuel(DIESEL, "10880"), fuel(GAS, "3070")),

            // ── SAN CRISTÓBAL / TUNJUELITO ────────────────────────────────────────
            station("EDS Esso San Cristóbal",
                    "Carrera 3 Este # 31-20 Sur, Bogotá",
                    4.560400, -74.072600, "San Cristóbal",
                    fuel(REGULAR, "15080"), fuel(DIESEL, "10920")),

            station("Terpel Tunjuelito",
                    "Carrera 18 # 48-30 Sur, Bogotá",
                    4.567100, -74.098400, "Tunjuelito",
                    fuel(REGULAR, "15050"), fuel(PREMIUM, "17300"), fuel(DIESEL, "10910")),

            // ── NORTE EXTREMO ─────────────────────────────────────────────────────
            station("Primax Autopista Norte km 7",
                    "Autopista Norte km 7, Bogotá",
                    4.776200, -74.042100, "Usaquén",
                    fuel(REGULAR, "15070"), fuel(PREMIUM, "17330"), fuel(DIESEL, "10960"), fuel(GAS, "3110")),

            station("Terpel La Caro",
                    "Autopista Norte km 10, Bogotá",
                    4.802300, -74.038700, "Usaquén",
                    fuel(REGULAR, "15040"), fuel(DIESEL, "10930")),

            // ── AV. BOYACÁ / TRANSVERSAL ──────────────────────────────────────────
            station("Biomax Av. Boyacá Sur",
                    "Avenida Boyacá # 9-20 Sur, Bogotá",
                    4.623400, -74.119800, "Kennedy",
                    fuel(REGULAR, "15060"), fuel(PREMIUM, "17310"), fuel(DIESEL, "10940")),

            station("EDS Gulf Av. Boyacá Norte",
                    "Avenida Boyacá # 80-10, Bogotá",
                    4.685600, -74.107300, "Engativá",
                    fuel(REGULAR, "15080"), fuel(DIESEL, "10960"), fuel(GAS, "3130")),

            station("Petrobras Av. Laureano Gómez",
                    "Avenida Laureano Gómez Cra 9 # 112-30, Bogotá",
                    4.700100, -74.032700, "Usaquén",
                    fuel(REGULAR, "15218"), fuel(PREMIUM, "17393"), fuel(DIESEL, "10903")),

            station("Primax Calle 170",
                    "Calle 170 # 54-20, Bogotá",
                    4.769400, -74.061200, "Suba",
                    fuel(REGULAR, "15090"), fuel(PREMIUM, "17360"), fuel(DIESEL, "10970"), fuel(GAS, "3160")),

            station("Terpel Portal 80",
                    "Calle 80 # 111-40, Bogotá",
                    4.714800, -74.127600, "Engativá",
                    fuel(REGULAR, "15070"), fuel(DIESEL, "10950"), fuel(GAS, "3140"))
        );
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private GasStationEntity station(String name, String address,
                                double lat, double lon, String zone,
                                FuelPriceEntity... fuels) {
        GasStationEntity s = new GasStationEntity();
        s.setName(name);
        s.setAddress(address);
        s.setLatitude(lat);
        s.setLongitude(lon);
        s.setZone(zone);
        s.setIsActive(true);
        s.setCreatedAt(LocalDateTime.now());

        List<FuelPriceEntity> fuelList = List.of(fuels);
        fuelList.forEach(f -> f.setGasStation(s));
        s.setFuels(fuelList);
        return s;
    }

    private FuelPriceEntity fuel(com.essence.ahorratank.fuel.FuelType type, String price) {
        FuelPriceEntity f = new FuelPriceEntity();
        f.setFuelType(type);
        f.setPricePerGallon(new BigDecimal(price));
        f.setIsAvailable(true);
        f.setUpdatedAt(LocalDateTime.now());
        return f;
    }
}
