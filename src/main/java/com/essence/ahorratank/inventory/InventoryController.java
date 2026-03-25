package com.essence.ahorratank.inventory;

import com.essence.ahorratank.inventoryLog.InventoryLogDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * UC-010 — Registrar entrada de combustible
     * POST /inventory/entry
     * Solo OPERATOR
     */
    @PostMapping("/entry")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<InventoryDTO> registerEntry(
            @RequestBody @Valid RegisterEntryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        InventoryDTO result = inventoryService.registerEntry(
                request, userDetails.getUsername()
        );
        return ResponseEntity.ok(result);
    }

    /**
     * UC-011 — Consultar inventario actual
     * GET /inventory
     * Solo OPERATOR
     */
    @GetMapping
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<List<InventoryDTO>> getInventory(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                inventoryService.getInventory(userDetails.getUsername())
        );
    }

    /**
     * Historial de movimientos (bitácora)
     * GET /inventory/log
     * Solo OPERATOR
     */
    @GetMapping("/log")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<List<InventoryLogDTO>> getLog(
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(
                inventoryService.getInventoryLog(userDetails.getUsername())
        );
    }
}