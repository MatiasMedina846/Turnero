package com.sistema.turnos.controller;

import com.sistema.turnos.dto.EmpleadoDTO;
import com.sistema.turnos.service.EmpleadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoDTO>> listar() {
        return ResponseEntity.ok(empleadoService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<EmpleadoDTO> crear(@RequestBody EmpleadoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoService.crear(dto));
    }
}
