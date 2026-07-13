package com.sistema.turnos.service;

import com.sistema.turnos.dto.TurnoRequestDTO;
import com.sistema.turnos.dto.TurnoResponseDTO;
import com.sistema.turnos.exception.OverlapException;
import com.sistema.turnos.model.Empleado;
import com.sistema.turnos.model.EstadoTurno;
import com.sistema.turnos.model.Servicio;
import com.sistema.turnos.model.Turno;
import com.sistema.turnos.repository.EmpleadoRepository;
import com.sistema.turnos.repository.ServicioRepository;
import com.sistema.turnos.repository.TurnoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ServicioRepository servicioRepository;

    public TurnoService(TurnoRepository turnoRepository, EmpleadoRepository empleadoRepository, ServicioRepository servicioRepository) {
        this.turnoRepository = turnoRepository;
        this.empleadoRepository = empleadoRepository;
        this.servicioRepository = servicioRepository;
    }

    public TurnoResponseDTO crear(TurnoRequestDTO request) {
        Empleado empleado = empleadoRepository.findById(request.getEmpleadoId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        Servicio servicio = servicioRepository.findById(request.getServicioId())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        LocalDateTime inicio = request.getFechaHoraInicio();
        if (inicio == null) {
            throw new RuntimeException("La fecha y hora de inicio es obligatoria.");
        }
        LocalDateTime fin = inicio.plusMinutes(servicio.getDuracionMinutos());

        validarOverlap(empleado.getId(), inicio, fin);

        Turno turno = new Turno();
        turno.setClienteNombre(request.getClienteNombre());
        turno.setClienteTelefono(request.getClienteTelefono());
        turno.setEmpleado(empleado);
        turno.setServicio(servicio);
        turno.setFechaHoraInicio(inicio);
        turno.setFechaHoraFin(fin);
        turno.setEstado(EstadoTurno.PENDIENTE);

        turno = turnoRepository.save(turno);
        return toResponseDTO(turno);
    }

    public TurnoResponseDTO actualizar(Long id, TurnoRequestDTO request) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        Empleado empleado = empleadoRepository.findById(request.getEmpleadoId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        Servicio servicio = servicioRepository.findById(request.getServicioId())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        LocalDateTime inicio = request.getFechaHoraInicio();
        if (inicio == null) {
            throw new RuntimeException("La fecha y hora de inicio es obligatoria.");
        }
        LocalDateTime fin = inicio.plusMinutes(servicio.getDuracionMinutos());

        boolean mismoEmpleado = turno.getEmpleado().getId().equals(empleado.getId());
        boolean mismoHorario = turno.getFechaHoraInicio().equals(inicio) && turno.getFechaHoraFin().equals(fin);
        if (!(mismoEmpleado && mismoHorario)) {
            validarOverlap(empleado.getId(), inicio, fin);
        }

        turno.setClienteNombre(request.getClienteNombre());
        turno.setClienteTelefono(request.getClienteTelefono());
        turno.setEmpleado(empleado);
        turno.setServicio(servicio);
        turno.setFechaHoraInicio(inicio);
        turno.setFechaHoraFin(fin);

        turno = turnoRepository.save(turno);
        return toResponseDTO(turno);
    }

    public void eliminar(Long id) {
        if (!turnoRepository.existsById(id)) {
            throw new RuntimeException("Turno no encontrado");
        }
        turnoRepository.deleteById(id);
    }

    public TurnoResponseDTO obtener(Long id) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        return toResponseDTO(turno);
    }

    public List<TurnoResponseDTO> listarTodos() {
        return turnoRepository.findAllByOrderByFechaHoraInicioAsc().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private void validarOverlap(Long empleadoId, LocalDateTime inicio, LocalDateTime fin) {
        boolean overlap = turnoRepository.existsOverlap(empleadoId, EstadoTurno.PENDIENTE, inicio, fin);
        if (overlap) {
            throw new OverlapException("El empleado seleccionado ya se encuentra ocupado en ese rango horario.");
        }
    }

    private TurnoResponseDTO toResponseDTO(Turno turno) {
        return new TurnoResponseDTO(
                turno.getId(),
                turno.getClienteNombre(),
                turno.getClienteTelefono(),
                turno.getEmpleado().getId(),
                turno.getEmpleado().getNombre(),
                turno.getServicio().getId(),
                turno.getServicio().getNombre(),
                turno.getFechaHoraInicio(),
                turno.getFechaHoraFin(),
                turno.getEstado().name()
        );
    }
}
