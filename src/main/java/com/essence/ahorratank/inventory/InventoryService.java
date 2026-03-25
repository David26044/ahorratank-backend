package com.essence.ahorratank.inventory;

import com.essence.ahorratank.gasStation.GasStationEntity;
import com.essence.ahorratank.gasStation.GasStationRepository;
import com.essence.ahorratank.inventoryLog.InventoryLogEntity;
import com.essence.ahorratank.inventoryLog.InventoryLogRepository;
import com.essence.ahorratank.inventoryLog.InventoryLogDTO;
import com.essence.ahorratank.user.UserEntity;
import com.essence.ahorratank.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository    inventoryRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final UserRepository         userRepository;
    private final GasStationRepository   gasStationRepository;

    // Umbral para estado "BAJO" — menos de 100 galones
    private static final BigDecimal LOW_STOCK_THRESHOLD = new BigDecimal("100");

    // ── UC-010 — Registrar entrada de combustible ─────────────
    @Transactional
    public InventoryDTO registerEntry(RegisterEntryRequest request, String operatorEmail) {

        // 1. Obtener el operador autenticado
        UserEntity operator = userRepository.findByEmail(operatorEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Operador no encontrado"));

        // 2. Verificar que el operador tiene estación asignada
        GasStationEntity station = operator.getGasStation();
        if (station == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Este operador no tiene estación asignada");
        }

        // 3. Buscar o crear el registro de inventario para ese combustible
        InventoryEntity inventory = inventoryRepository
                .findByGasStationIdAndFuelType(station.getId(), request.fuelType())
                .orElseGet(() -> {
                    InventoryEntity newInventory = new InventoryEntity();
                    newInventory.setGasStation(station);
                    newInventory.setFuelType(request.fuelType());
                    newInventory.setQuantityGallons(BigDecimal.ZERO);
                    return newInventory;
                });

        // 4. Sumar la cantidad al inventario actual
        inventory.setQuantityGallons(
                inventory.getQuantityGallons().add(request.quantityGallons())
        );
        inventoryRepository.save(inventory);

        // 5. Registrar en bitácora
        InventoryLogEntity log = new InventoryLogEntity();
        log.setGasStation(station);
        log.setOperator(operator);
        log.setFuelType(request.fuelType());
        log.setQuantityAdded(request.quantityGallons());
        log.setInvoiceNumber(request.invoiceNumber());
        inventoryLogRepository.save(log);

        // 6. Devolver el inventario actualizado como DTO
        return toDTO(inventory);
    }

    // ── UC-011 — Consultar inventario de la estación ──────────
    public List<InventoryDTO> getInventory(String operatorEmail) {

        UserEntity operator = userRepository.findByEmail(operatorEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Operador no encontrado"));

        GasStationEntity station = operator.getGasStation();
        if (station == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Este operador no tiene estación asignada");
        }

        List<InventoryEntity> inventory =
                inventoryRepository.findByGasStationId(station.getId());

        if (inventory.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No hay inventario registrado");
        }

        return inventory.stream()
                .map(this::toDTO)
                .toList();
    }

    // ── Historial de movimientos ──────────────────────────────
    public List<InventoryLogDTO> getInventoryLog(String operatorEmail) {

        UserEntity operator = userRepository.findByEmail(operatorEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Operador no encontrado"));

        GasStationEntity station = operator.getGasStation();
        if (station == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Este operador no tiene estación asignada");
        }

        return inventoryLogRepository
                .findByGasStationIdOrderByCreatedAtDesc(station.getId())
                .stream()
                .map(this::toLogDTO)
                .toList();
    }

    // ── Mappers privados ──────────────────────────────────────

    private InventoryDTO toDTO(InventoryEntity e) {
        return new InventoryDTO(
                e.getFuelType(),
                e.getQuantityGallons(),
                resolveStatus(e.getQuantityGallons()),
                e.getUpdatedAt()
        );
    }

    private InventoryLogDTO toLogDTO(InventoryLogEntity e) {
        return new InventoryLogDTO(
                e.getId(),
                e.getFuelType(),
                e.getQuantityAdded(),
                e.getInvoiceNumber(),
                e.getOperator().getFirstName() + " " + e.getOperator().getLastName(),
                e.getCreatedAt()
        );
    }

    private String resolveStatus(BigDecimal quantity) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) return "SIN STOCK";
        if (quantity.compareTo(LOW_STOCK_THRESHOLD) < 0)  return "BAJO";
        return "DISPONIBLE";
    }
}