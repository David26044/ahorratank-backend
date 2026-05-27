-- ── Usuario operador asociado a la estación 1 (Terpel Autopista Norte) ──
INSERT INTO users (first_name, last_name, email, password, enabled, role_id, gas_station_id)
VALUES (
           'Carlos',
           'Operador',
           'operador@test.com',
           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lbiW',
           true,
           (SELECT id FROM roles WHERE name = 'OPERATOR'),
           (SELECT id FROM gas_station WHERE name = 'Terpel Autopista Norte' LIMIT 1)
    );
