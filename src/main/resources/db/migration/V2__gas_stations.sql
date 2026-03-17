-- V2__gas_stations.sql (Flyway) o ejecuta directo

CREATE TABLE gas_station (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(150)     NOT NULL,
    address     VARCHAR(255)     NOT NULL,
    latitude    DOUBLE PRECISION NOT NULL,
    longitude   DOUBLE PRECISION NOT NULL,
    zone        VARCHAR(100)     NOT NULL,
    is_active   BOOLEAN          NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP        NOT NULL DEFAULT NOW()
);

CREATE TABLE fuel_price (
    id               BIGSERIAL PRIMARY KEY,
    gas_station_id   BIGINT          NOT NULL REFERENCES gas_station(id),
    fuel_type        VARCHAR(20)     NOT NULL,  -- REGULAR, PREMIUM, DIESEL, GAS
    price_per_gallon NUMERIC(10, 2)  NOT NULL,
    is_available     BOOLEAN         NOT NULL DEFAULT TRUE,
    updated_at       TIMESTAMP       NOT NULL DEFAULT NOW(),
    UNIQUE (gas_station_id, fuel_type)
);

-- Índices útiles para los filtros
CREATE INDEX idx_station_zone     ON gas_station(zone);
CREATE INDEX idx_station_active   ON gas_station(is_active);
CREATE INDEX idx_fuel_type        ON fuel_price(fuel_type);
CREATE INDEX idx_fuel_available   ON fuel_price(is_available);