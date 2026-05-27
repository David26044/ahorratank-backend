package com.essence.ahorratank.inventory;

import com.essence.ahorratank.fuel.FuelType;
import com.essence.ahorratank.gasStation.GasStationEntity;
import com.essence.ahorratank.gasStation.GasStationRepository;
import com.essence.ahorratank.inventoryLog.InventoryLogEntity;
import com.essence.ahorratank.inventoryLog.InventoryLogRepository;
import com.essence.ahorratank.inventoryLog.InventoryLogDTO;
import com.essence.ahorratank.user.UserEntity;
import com.essence.ahorratank.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.openpdf.text.*;
import org.openpdf.text.Document;
import org.openpdf.text.PageSize;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public byte[] generateInventoryReport(Authentication authentication, FuelType fuelType) {
        validateOperatorRole(authentication);

        UserEntity user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario autenticado no encontrado"));

        if (user.getGasStation() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El operador no tiene una estación asignada");
        }

        Long gasStationId = user.getGasStation().getId();

        List<InventoryEntity> inventoryList = (fuelType == null)
                ? inventoryRepository.findByGasStationId(gasStationId)
                : inventoryRepository.findAllByGasStationIdAndFuelType(gasStationId, fuelType);

        if (inventoryList.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No existen datos de inventario para generar el reporte");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            addTitle(document, user, fuelType);
            addMetadataSection(document, user, fuelType, inventoryList.size());
            addInventoryTable(document, inventoryList);

            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar el reporte PDF");
        }
    }

    private void validateOperatorRole(Authentication authentication) {
        boolean isOperator = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_OPERATOR"));

        if (!isOperator) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No tienes permisos para generar este reporte");
        }
    }

    private void addTitle(Document document, UserEntity user, FuelType fuelType) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

        Paragraph title = new Paragraph("Reporte del inventario actual", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(8f);
        document.add(title);

        String stationName = user.getGasStation().getName() != null
                ? user.getGasStation().getName()
                : "Estación #" + user.getGasStation().getId();

        Paragraph subtitle = new Paragraph(
                "Estación: " + stationName,
                subtitleFont
        );
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(14f);
        document.add(subtitle);

        if (fuelType != null) {
            Paragraph filter = new Paragraph("Filtro aplicado: " + fuelType.name(), subtitleFont);
            filter.setAlignment(Element.ALIGN_CENTER);
            filter.setSpacingAfter(10f);
            document.add(filter);
        }
    }

    private void addMetadataSection(Document document, UserEntity user, FuelType fuelType, int totalRows)
            throws DocumentException {

        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        String generatedAt = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        document.add(new Paragraph("Fecha de generación: " + generatedAt, normalFont));
        document.add(new Paragraph("Operador: " + user.getFirstName() + " " + user.getLastName(), normalFont));
        document.add(new Paragraph("Correo: " + user.getEmail(), normalFont));
        document.add(new Paragraph(
                "Tipo de combustible: " + (fuelType != null ? fuelType.name() : "TODOS"),
                normalFont
        ));
        document.add(new Paragraph("Registros incluidos: " + totalRows, normalFont));
        document.add(Chunk.NEWLINE);
    }

    private void addInventoryTable(Document document, List<InventoryEntity> inventoryList)
            throws DocumentException {

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5f);
        table.setWidths(new float[]{3f, 2f, 3f});



        addHeaderCell(table, "Combustible");
        addHeaderCell(table, "Cantidad actual");
        addHeaderCell(table, "Última actualización");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (InventoryEntity inventory : inventoryList) {
            table.addCell(safeText(inventory.getFuelType() != null ? inventory.getFuelType().name() : ""));
            table.addCell(safeText(String.valueOf(inventory.getQuantityGallons())));
            table.addCell(safeText(
                    inventory.getUpdatedAt() != null
                            ? inventory.getUpdatedAt().format(formatter)
                            : "Sin registro"
            ));
        }

        document.add(table);
    }

    private void addHeaderCell(PdfPTable table, String text) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        PdfPCell cell = new PdfPCell(new Phrase(text, headerFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8f);
        table.addCell(cell);
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }
}