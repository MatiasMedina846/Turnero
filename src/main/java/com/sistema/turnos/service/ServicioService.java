package com.sistema.turnos.service;

import com.sistema.turnos.dto.ServicioDTO;
import com.sistema.turnos.model.Servicio;
import com.sistema.turnos.repository.ServicioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;

    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    public List<ServicioDTO> listarTodos() {
        return servicioRepository.findAll().stream()
                .map(s -> new ServicioDTO(s.getId(), s.getNombre(), s.getDuracionMinutos()))
                .toList();
    }

    public ServicioDTO crear(ServicioDTO dto) {
        Servicio servicio = new Servicio(dto.getNombre(), dto.getDuracionMinutos());
        servicio = servicioRepository.save(servicio);
        return new ServicioDTO(servicio.getId(), servicio.getNombre(), servicio.getDuracionMinutos());
    }
}
