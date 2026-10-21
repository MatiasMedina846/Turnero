package com.sistema.turnos.controller;

import com.sistema.turnos.dto.TurnoRequestDTO;
import com.sistema.turnos.dto.TurnoResponseDTO;
import com.sistema.turnos.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @GetMapping
    public ResponseEntity<List<TurnoResponseDTO>> listar() {
        return ResponseEntity.ok(turnoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurnoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<TurnoResponseDTO> crear(@Valid @RequestBody TurnoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(turnoService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurnoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody TurnoRequestDTO request) {
        return ResponseEntity.ok(turnoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        turnoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
