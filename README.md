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

## Configuración

Copia `.env.example` como referencia para configurar variables locales o del proveedor cloud.

Variables principales para despliegue:

| Variable | Descripción |
|---|---|
| `PORT` | Puerto asignado por el proveedor. Local: `8080` |
| `SPRING_DATASOURCE_URL` | URL JDBC de PostgreSQL. Ej: `jdbc:postgresql://host:5432/db` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de PostgreSQL |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de PostgreSQL |
| `APP_CORS_ALLOWED_ORIGINS` | Dominios permitidos del front, separados por coma |
| `JWT_SECRET` | Secreto Base64 para firmar JWT |

Para Render, crear primero una base PostgreSQL y luego un Web Service del backend. En variables de entorno usa la URL JDBC de la base y agrega el dominio del frontend en `APP_CORS_ALLOWED_ORIGINS`.

Comandos sugeridos para Render:

```bash
./mvnw -DskipTests package
java -jar target/ahorra-tank-0.0.1-SNAPSHOT.jar
```

---

## Endpoints principales

Base URL local: `http://localhost:8080/system/api`

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
