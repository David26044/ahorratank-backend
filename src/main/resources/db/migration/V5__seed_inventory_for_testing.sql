-- Seed de inventario para pruebas.
-- Crea inventario para todos los combustibles publicados por cada estacion.

INSERT INTO inventory (gas_station_id, fuel_type, quantity_gallons, updated_at)
SELECT
    fp.gas_station_id,
    fp.fuel_type,
    CASE fp.fuel_type
        WHEN 'REGULAR' THEN 520.00
        WHEN 'PREMIUM' THEN 310.00
        WHEN 'DIESEL' THEN 430.00
        WHEN 'GAS' THEN 180.00
        ELSE 250.00
    END,
    NOW()
FROM fuel_price fp
ON CONFLICT (gas_station_id, fuel_type)
DO NOTHING;

-- Asegura datos completos y faciles de validar para la estacion del operador de prueba.
INSERT INTO inventory (gas_station_id, fuel_type, quantity_gallons, updated_at)
SELECT
    u.gas_station_id,
    fuel_type,
    quantity_gallons,
    NOW()
FROM users u
CROSS JOIN (
    VALUES
        ('REGULAR', 850.00),
        ('PREMIUM', 620.00),
        ('DIESEL', 710.00),
        ('GAS', 240.00)
) AS seeded_inventory(fuel_type, quantity_gallons)
WHERE u.email = 'operador@test.com'
  AND u.gas_station_id IS NOT NULL
ON CONFLICT (gas_station_id, fuel_type)
DO UPDATE SET
    quantity_gallons = EXCLUDED.quantity_gallons,
    updated_at = NOW();
