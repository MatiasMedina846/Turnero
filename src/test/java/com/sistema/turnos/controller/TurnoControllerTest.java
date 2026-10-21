package com.sistema.turnos.controller;

import com.sistema.turnos.exception.GlobalExceptionHandler;
import com.sistema.turnos.exception.OverlapException;
import com.sistema.turnos.exception.ResourceNotFoundException;
import com.sistema.turnos.service.TurnoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Checks how errors are translated to HTTP responses, without starting the whole application. */
class TurnoControllerTest {

    private static final String BODY_VALIDO = """
            {"clienteNombre":"Ana","empleadoId":1,"servicioId":2,"fechaHoraInicio":"2030-01-15T10:00:00"}""";

    private TurnoService turnoService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        turnoService = Mockito.mock(TurnoService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new TurnoController(turnoService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void solapamientoDevuelve409() throws Exception {
        when(turnoService.crear(any())).thenThrow(new OverlapException("El empleado seleccionado ya se encuentra ocupado en ese rango horario."));

        mockMvc.perform(post("/api/turnos").contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("ocupado")));
    }

    @Test
    void turnoInexistenteDevuelve404() throws Exception {
        when(turnoService.obtener(99L)).thenThrow(new ResourceNotFoundException("Turno no encontrado"));

        mockMvc.perform(get("/api/turnos/99"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("Turno no encontrado")));
    }

    @Test
    void cuerpoSinCamposObligatoriosDevuelve400() throws Exception {
        mockMvc.perform(post("/api/turnos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
}
