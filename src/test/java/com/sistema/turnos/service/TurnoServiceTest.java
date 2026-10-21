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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnoServiceTest {

    private static final LocalDateTime INICIO = LocalDateTime.of(2030, 1, 15, 10, 0);

    @Mock
    private TurnoRepository turnoRepository;
    @Mock
    private EmpleadoRepository empleadoRepository;
    @Mock
    private ServicioRepository servicioRepository;

    @InjectMocks
    private TurnoService turnoService;

    private Empleado empleado;
    private Servicio servicio;

    @BeforeEach
    void setUp() {
        empleado = new Empleado("Carlos", "Corte");
        empleado.setId(1L);
        servicio = new Servicio("Corte Clásico", 30);
        servicio.setId(2L);
    }

    private TurnoRequestDTO request() {
        TurnoRequestDTO dto = new TurnoRequestDTO();
        dto.setClienteNombre("Ana");
        dto.setClienteTelefono("3534000000");
        dto.setEmpleadoId(1L);
        dto.setServicioId(2L);
        dto.setFechaHoraInicio(INICIO);
        return dto;
    }

    @Test
    void crear_calculaLaHoraDeFinSegunLaDuracionDelServicio() {
        when(empleadoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(empleado));
        when(servicioRepository.findById(2L)).thenReturn(Optional.of(servicio));
        when(turnoRepository.existsOverlap(eq(1L), eq(EstadoTurno.PENDIENTE), any(), any())).thenReturn(false);
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));

        TurnoResponseDTO result = turnoService.crear(request());

        assertEquals(INICIO, result.getFechaHoraInicio());
        assertEquals(INICIO.plusMinutes(30), result.getFechaHoraFin());
        assertEquals("PENDIENTE", result.getEstado());
    }

    @Test
    void crear_rechazaElTurnoSiElEmpleadoYaEstaOcupado() {
        when(empleadoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(empleado));
        when(servicioRepository.findById(2L)).thenReturn(Optional.of(servicio));
        when(turnoRepository.existsOverlap(1L, EstadoTurno.PENDIENTE, INICIO, INICIO.plusMinutes(30))).thenReturn(true);

        assertThrows(OverlapException.class, () -> turnoService.crear(request()));

        verify(turnoRepository, never()).save(any());
    }

    @Test
    void crear_fallaSiElEmpleadoNoExiste() {
        when(empleadoRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> turnoService.crear(request()));
    }

    @Test
    void crear_fallaSiElServicioNoExiste() {
        when(empleadoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(empleado));
        when(servicioRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> turnoService.crear(request()));
    }

    @Test
    void actualizar_noSeConsideraAsiMismoUnConflicto() {
        Turno existente = new Turno();
        existente.setId(10L);
        existente.setEmpleado(empleado);
        existente.setServicio(servicio);
        existente.setFechaHoraInicio(INICIO);
        existente.setFechaHoraFin(INICIO.plusMinutes(30));
        existente.setEstado(EstadoTurno.PENDIENTE);

        when(turnoRepository.findById(10L)).thenReturn(Optional.of(existente));
        when(empleadoRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(empleado));
        when(servicioRepository.findById(2L)).thenReturn(Optional.of(servicio));
        when(turnoRepository.existsOverlapExcluding(1L, EstadoTurno.PENDIENTE, 10L, INICIO.plusMinutes(15), INICIO.plusMinutes(45)))
                .thenReturn(false);
        when(turnoRepository.save(any(Turno.class))).thenAnswer(inv -> inv.getArgument(0));

        TurnoRequestDTO dto = request();
        dto.setFechaHoraInicio(INICIO.plusMinutes(15));

        TurnoResponseDTO result = turnoService.actualizar(10L, dto);

        assertEquals(INICIO.plusMinutes(15), result.getFechaHoraInicio());
        verify(turnoRepository, never()).existsOverlap(any(), any(), any(), any());
    }

    @Test
    void eliminar_fallaSiElTurnoNoExiste() {
        when(turnoRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> turnoService.eliminar(99L));
        verify(turnoRepository, never()).deleteById(any());
    }
}
