package com.sistema.turnos.dto;

import java.time.LocalDateTime;

public class TurnoResponseDTO {

    private Long id;
    private String clienteNombre;
    private String clienteTelefono;
    private Long empleadoId;
    private String empleadoNombre;
    private Long servicioId;
    private String servicioNombre;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private String estado;

    public TurnoResponseDTO() {}

    public TurnoResponseDTO(Long id, String clienteNombre, String clienteTelefono, Long empleadoId, String empleadoNombre, Long servicioId, String servicioNombre, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, String estado) {
        this.id = id;
        this.clienteNombre = clienteNombre;
        this.clienteTelefono = clienteTelefono;
        this.empleadoId = empleadoId;
        this.empleadoNombre = empleadoNombre;
        this.servicioId = servicioId;
        this.servicioNombre = servicioNombre;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getClienteTelefono() { return clienteTelefono; }
    public void setClienteTelefono(String clienteTelefono) { this.clienteTelefono = clienteTelefono; }
    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }
    public String getEmpleadoNombre() { return empleadoNombre; }
    public void setEmpleadoNombre(String empleadoNombre) { this.empleadoNombre = empleadoNombre; }
    public Long getServicioId() { return servicioId; }
    public void setServicioId(Long servicioId) { this.servicioId = servicioId; }
    public String getServicioNombre() { return servicioNombre; }
    public void setServicioNombre(String servicioNombre) { this.servicioNombre = servicioNombre; }
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) { this.fechaHoraInicio = fechaHoraInicio; }
    public LocalDateTime getFechaHoraFin() { return fechaHoraFin; }
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) { this.fechaHoraFin = fechaHoraFin; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
