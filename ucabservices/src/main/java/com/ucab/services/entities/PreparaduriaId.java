package com.ucab.services.entities;

import java.io.Serializable;
import java.util.Objects;

public class PreparaduriaId implements Serializable {

    // JPA mapeará automáticamente esto a la llave compuesta de Estudiante
    private PeriodoVinculacionId estudiante; 
    private String asignaturaAsignada;

    public PreparaduriaId() {}

    // Getters, Setters, Equals y HashCode (Obligatorios)
    public PeriodoVinculacionId getEstudiante() { return estudiante; }
    public void setEstudiante(PeriodoVinculacionId estudiante) { this.estudiante = estudiante; }

    public String getAsignaturaAsignada() { return asignaturaAsignada; }
    public void setAsignaturaAsignada(String asignaturaAsignada) { this.asignaturaAsignada = asignaturaAsignada; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PreparaduriaId that = (PreparaduriaId) o;
        return Objects.equals(estudiante, that.estudiante) && Objects.equals(asignaturaAsignada, that.asignaturaAsignada);
    }

    @Override
    public int hashCode() {
        return Objects.hash(estudiante, asignaturaAsignada);
    }
}