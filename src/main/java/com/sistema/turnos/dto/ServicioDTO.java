package com.sistema.turnos.dto;

public class ServicioDTO {

    private Long id;
    private String nombre;
    private Integer duracionMinutos;

    public ServicioDTO() {}

    public ServicioDTO(Long id, String nombre, Integer duracionMinutos) {
        this.id = id;
        this.nombre = nombre;
        this.duracionMinutos = duracionMinutos;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer duracionMinutos) { this.duracionMinutos = duracionMinutos; }
}
