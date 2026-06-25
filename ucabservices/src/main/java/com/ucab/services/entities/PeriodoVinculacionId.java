package com.ucab.services.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class PeriodoVinculacionId implements Serializable {

    private Long usuario; // Debe llamarse exactamente igual que el atributo en la entidad principal
    private LocalDate fechaInicio;

    public PeriodoVinculacionId() {}

    public PeriodoVinculacionId(Long usuario, LocalDate fechaInicio) {
        this.usuario = usuario;
        this.fechaInicio = fechaInicio;
    }

    // Getters y Setters
    public Long getUsuario() { return usuario; }
    public void setUsuario(Long usuario) { this.usuario = usuario; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    // Equals y HashCode son obligatorios para llaves compuestas en JPA
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeriodoVinculacionId that = (PeriodoVinculacionId) o;
        return Objects.equals(usuario, that.usuario) && Objects.equals(fechaInicio, that.fechaInicio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario, fechaInicio);
    }
}