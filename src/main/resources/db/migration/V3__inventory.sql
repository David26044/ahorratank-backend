-- 1. Asociar operador a estación (columna nueva en users)
ALTER TABLE users
    ADD COLUMN gas_station_id BIGINT,
ADD CONSTRAINT fk_users_gas_station
    FOREIGN KEY (gas_station_id)
    REFERENCES gas_station(id);

-- 2. Inventario actual por estación y tipo de combustible
CREATE TABLE inventory
(
    id               BIGSERIAL PRIMARY KEY,
    gas_station_id   BIGINT         NOT NULL REFERENCES gas_station (id),
    fuel_type        VARCHAR(20)    NOT NULL,
    quantity_gallons NUMERIC(12, 2) NOT NULL DEFAULT 0,
    updated_at       TIMESTAMP      NOT NULL DEFAULT NOW(),
    UNIQUE (gas_station_id, fuel_type)
);

-- 3. Bitácora de auditoría
CREATE TABLE inventory_log
(
    id             BIGSERIAL PRIMARY KEY,
    gas_station_id BIGINT         NOT NULL REFERENCES gas_station (id),
    operator_id    BIGINT         NOT NULL REFERENCES users (id),
    fuel_type      VARCHAR(20)    NOT NULL,
    quantity_added NUMERIC(12, 2) NOT NULL,
    invoice_number VARCHAR(100)   NOT NULL,
    created_at     TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_inventory_station ON inventory (gas_station_id);
CREATE INDEX idx_inv_log_station ON inventory_log (gas_station_id);
CREATE INDEX idx_inv_log_operator ON inventory_log (operator_id);
CREATE INDEX idx_inv_log_created ON inventory_log (created_at);