package com.ucab.services.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class HistorialSesionId implements Serializable {

    private Long usuario; // Corresponde al ID del Usuario
    private LocalDateTime fechaHoraAcceso; // Corresponde a la fecha y hora

    public HistorialSesionId() {}

    public HistorialSesionId(Long usuario, LocalDateTime fechaHoraAcceso) {
        this.usuario = usuario;
        this.fechaHoraAcceso = fechaHoraAcceso;
    }

    public Long getUsuario() { return usuario; }
    public void setUsuario(Long usuario) { this.usuario = usuario; }

    public LocalDateTime getFechaHoraAcceso() { return fechaHoraAcceso; }
    public void setFechaHoraAcceso(LocalDateTime fechaHoraAcceso) { this.fechaHoraAcceso = fechaHoraAcceso; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistorialSesionId that = (HistorialSesionId) o;
        return Objects.equals(usuario, that.usuario) &&
               Objects.equals(fechaHoraAcceso, that.fechaHoraAcceso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario, fechaHoraAcceso);
    }
}