package com.sistema.turnos.controller;

import com.sistema.turnos.dto.ServicioDTO;
import com.sistema.turnos.service.ServicioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping
    public ResponseEntity<List<ServicioDTO>> listar() {
        return ResponseEntity.ok(servicioService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<ServicioDTO> crear(@RequestBody ServicioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicioService.crear(dto));
    }
}
