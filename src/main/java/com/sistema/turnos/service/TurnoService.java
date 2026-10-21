package com.sistema.turnos.service;

import com.sistema.turnos.dto.TurnoRequestDTO;
import com.sistema.turnos.dto.TurnoResponseDTO;
import com.sistema.turnos.exception.OverlapException;
import com.sistema.turnos.exception.ResourceNotFoundException;
import com.sistema.turnos.model.Empleado;
import com.sistema.turnos.model.EstadoTurno;
import com.sistema.turnos.model.Servicio;
import com.sistema.turnos.model.Turno;
import com.sistema.turnos.repository.EmpleadoRepository;
import com.sistema.turnos.repository.ServicioRepository;
import com.sistema.turnos.repository.TurnoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
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
        // The employee row is locked until the transaction ends, so the overlap check and the
        // insert below are atomic with respect to other bookings of the same employee.
        Empleado empleado = empleadoRepository.findByIdForUpdate(request.getEmpleadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));
        Servicio servicio = buscarServicio(request.getServicioId());

        LocalDateTime inicio = request.getFechaHoraInicio();
        LocalDateTime fin = inicio.plusMinutes(servicio.getDuracionMinutos());

        validarOverlap(empleado.getId(), null, inicio, fin);

        Turno turno = new Turno();
        turno.setClienteNombre(request.getClienteNombre());
        turno.setClienteTelefono(request.getClienteTelefono());
        turno.setEmpleado(empleado);
        turno.setServicio(servicio);
        turno.setFechaHoraInicio(inicio);
        turno.setFechaHoraFin(fin);
        turno.setEstado(EstadoTurno.PENDIENTE);

        return toResponseDTO(turnoRepository.save(turno));
    }

    public TurnoResponseDTO actualizar(Long id, TurnoRequestDTO request) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado"));

        Empleado empleado = empleadoRepository.findByIdForUpdate(request.getEmpleadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));
        Servicio servicio = buscarServicio(request.getServicioId());

        LocalDateTime inicio = request.getFechaHoraInicio();
        LocalDateTime fin = inicio.plusMinutes(servicio.getDuracionMinutos());

        // The appointment being edited must not count as a conflict with itself.
        validarOverlap(empleado.getId(), turno.getId(), inicio, fin);

        turno.setClienteNombre(request.getClienteNombre());
        turno.setClienteTelefono(request.getClienteTelefono());
        turno.setEmpleado(empleado);
        turno.setServicio(servicio);
        turno.setFechaHoraInicio(inicio);
        turno.setFechaHoraFin(fin);

        return toResponseDTO(turnoRepository.save(turno));
    }

    public void eliminar(Long id) {
        if (!turnoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Turno no encontrado");
        }
        turnoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public TurnoResponseDTO obtener(Long id) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado"));
        return toResponseDTO(turno);
    }

    @Transactional(readOnly = true)
    public List<TurnoResponseDTO> listarTodos() {
        return turnoRepository.findAllByOrderByFechaHoraInicioAsc().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private Servicio buscarServicio(Long servicioId) {
        return servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));
    }

    private void validarOverlap(Long empleadoId, Long turnoIdAExcluir, LocalDateTime inicio, LocalDateTime fin) {
        boolean overlap = turnoIdAExcluir == null
                ? turnoRepository.existsOverlap(empleadoId, EstadoTurno.PENDIENTE, inicio, fin)
                : turnoRepository.existsOverlapExcluding(empleadoId, EstadoTurno.PENDIENTE, turnoIdAExcluir, inicio, fin);
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
