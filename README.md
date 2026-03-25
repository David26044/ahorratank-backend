# Ahorra Tank — Backend

API REST para consultar y comparar precios de combustible en estaciones de servicio de Bogotá.

> ⚠️ Proyecto en desarrollo — la estructura y endpoints pueden cambiar.

---

## Tecnologías

- Java 21 + Spring Boot 3.5
- Spring Security + JWT
- PostgreSQL + Flyway
- Lombok / Maven

---

## Requisitos

- Java 21+
- PostgreSQL corriendo localmente

---

## Endpoints principales

Base URL: `http://localhost:8080/api/v1`

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/stations` | Listar estaciones (filtros opcionales: zona, tipo combustible, precio) |
| `GET` | `/stations/nearby` | Estaciones cercanas por coordenadas |
| `GET` | `/stations/{id}` | Detalle de una estación |
| `GET` | `/stations/{id}/route` | Enlace de ruta en Google Maps |
| `GET` | `/stations/zones` | Zonas disponibles |
| `POST` | `/auth/register` | Registrar usuario |
| `POST` | `/auth/login` | Obtener token JWT |

---

## Tipos de combustible

`REGULAR` · `PREMIUM` · `DIESEL` · `GAS`
