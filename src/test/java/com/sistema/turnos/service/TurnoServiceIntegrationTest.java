package com.sistema.turnos.service;

import com.sistema.turnos.dto.TurnoRequestDTO;
import com.sistema.turnos.dto.TurnoResponseDTO;
import com.sistema.turnos.exception.OverlapException;
import com.sistema.turnos.model.Empleado;
import com.sistema.turnos.model.Servicio;
import com.sistema.turnos.repository.EmpleadoRepository;
import com.sistema.turnos.repository.ServicioRepository;
import com.sistema.turnos.repository.TurnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Runs the real service against an in-memory H2 database (see src/test/resources). */
@SpringBootTest
class TurnoServiceIntegrationTest {

    private static final LocalDateTime INICIO = LocalDateTime.of(2030, 1, 15, 10, 0);

    @Autowired
    private TurnoService turnoService;
    @Autowired
    private TurnoRepository turnoRepository;
    @Autowired
    private EmpleadoRepository empleadoRepository;
    @Autowired
    private ServicioRepository servicioRepository;

    private Long empleadoId;
    private Long servicioId;

    @BeforeEach
    void setUp() {
        turnoRepository.deleteAll();
        empleadoId = empleadoRepository.save(new Empleado("Test Empleado", "Test")).getId();
        servicioId = servicioRepository.save(new Servicio("Servicio 60", 60)).getId();
    }

    private TurnoRequestDTO request(LocalDateTime inicio) {
        TurnoRequestDTO dto = new TurnoRequestDTO();
        dto.setClienteNombre("Cliente");
        dto.setEmpleadoId(empleadoId);
        dto.setServicioId(servicioId);
        dto.setFechaHoraInicio(inicio);
        return dto;
    }

    @Test
    void rechazaTurnosSolapados() {
        turnoService.crear(request(INICIO));

        assertThrows(OverlapException.class, () -> turnoService.crear(request(INICIO.plusMinutes(30))));
        assertThrows(OverlapException.class, () -> turnoService.crear(request(INICIO.minusMinutes(30))));
        assertThrows(OverlapException.class, () -> turnoService.crear(request(INICIO)));
    }

    @Test
    void permiteTurnosConsecutivosSinSolapamiento() {
        TurnoResponseDTO primero = turnoService.crear(request(INICIO));

        TurnoResponseDTO siguiente = turnoService.crear(request(primero.getFechaHoraFin()));
        TurnoResponseDTO anterior = turnoService.crear(request(INICIO.minusMinutes(60)));

        assertEquals(3, turnoRepository.count());
        assertEquals(INICIO.plusMinutes(60), siguiente.getFechaHoraInicio());
        assertEquals(INICIO, anterior.getFechaHoraFin());
    }

    @Test
    void permiteReprogramarUnTurnoSobreSuPropioHorario() {
        TurnoResponseDTO turno = turnoService.crear(request(INICIO));

        TurnoResponseDTO movido = turnoService.actualizar(turno.getId(), request(INICIO.plusMinutes(30)));

        assertEquals(INICIO.plusMinutes(30), movido.getFechaHoraInicio());
    }

    @Test
    void dosReservasSimultaneasDelMismoHorarioSoloUnaGana() throws Exception {
        int intentos = 6;
        ExecutorService pool = Executors.newFixedThreadPool(intentos);
        CountDownLatch salida = new CountDownLatch(1);
        List<Future<Boolean>> resultados = new ArrayList<>();

        for (int i = 0; i < intentos; i++) {
            Callable<Boolean> reserva = () -> {
                salida.await();
                try {
                    turnoService.crear(request(INICIO));
                    return true;
                } catch (OverlapException e) {
                    return false;
                }
            };
            resultados.add(pool.submit(reserva));
        }
        salida.countDown();

        long exitosas = 0;
        for (Future<Boolean> f : resultados) {
            try {
                if (f.get()) {
                    exitosas++;
                }
            } catch (ExecutionException e) {
                throw new AssertionError("Una reserva fallo con un error inesperado", e.getCause());
            }
        }
        pool.shutdown();

        assertEquals(1, exitosas);
        assertEquals(1, turnoRepository.count());
    }
}
