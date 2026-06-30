package com.ucab.services.entities;

import java.io.Serializable;
import java.util.Objects;

public class BecaId implements Serializable {

    private PeriodoVinculacionId estudiante;

    public BecaId() {}

    public PeriodoVinculacionId getEstudiante() { return estudiante; }
    public void setEstudiante(PeriodoVinculacionId estudiante) { this.estudiante = estudiante; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BecaId becaId = (BecaId) o;
        return Objects.equals(estudiante, becaId.estudiante);
    }

    @Override
    public int hashCode() {
        return Objects.hash(estudiante);
    }
}