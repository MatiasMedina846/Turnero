package com.sistema.turnos.service;

import com.sistema.turnos.dto.EmpleadoDTO;
import com.sistema.turnos.model.Empleado;
import com.sistema.turnos.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public List<EmpleadoDTO> listarTodos() {
        return empleadoRepository.findAll().stream()
                .map(e -> new EmpleadoDTO(e.getId(), e.getNombre(), e.getEspecialidad()))
                .toList();
    }

    public EmpleadoDTO crear(EmpleadoDTO dto) {
        Empleado empleado = new Empleado(dto.getNombre(), dto.getEspecialidad());
        empleado = empleadoRepository.save(empleado);
        return new EmpleadoDTO(empleado.getId(), empleado.getNombre(), empleado.getEspecialidad());
    }
}
