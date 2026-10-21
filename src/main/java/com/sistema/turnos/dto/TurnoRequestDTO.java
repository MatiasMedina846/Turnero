package com.sistema.turnos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class TurnoRequestDTO {

    @NotBlank(message = "el nombre del cliente es obligatorio")
    private String clienteNombre;
    private String clienteTelefono;
    @NotNull(message = "el empleado es obligatorio")
    private Long empleadoId;
    @NotNull(message = "el servicio es obligatorio")
    private Long servicioId;
    @NotNull(message = "la fecha y hora de inicio es obligatoria")
    private LocalDateTime fechaHoraInicio;

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getClienteTelefono() { return clienteTelefono; }
    public void setClienteTelefono(String clienteTelefono) { this.clienteTelefono = clienteTelefono; }
    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }
    public Long getServicioId() { return servicioId; }
    public void setServicioId(Long servicioId) { this.servicioId = servicioId; }
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) { this.fechaHoraInicio = fechaHoraInicio; }
}
