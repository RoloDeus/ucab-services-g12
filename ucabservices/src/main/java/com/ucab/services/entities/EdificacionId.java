package com.ucab.services.entities;

import java.io.Serializable;
import java.util.Objects;

public class EdificacionId implements Serializable {
    
    private String nombreSede;
    private String nombreEdificio;
    private String direccionInterna;

    public EdificacionId() {}

    public EdificacionId(String nombreSede, String nombreEdificio, String direccionInterna) {
        this.nombreSede = nombreSede;
        this.nombreEdificio = nombreEdificio;
        this.direccionInterna = direccionInterna;
    }

    public String getNombreSede() { return nombreSede; }
    public void setNombreSede(String nombreSede) { this.nombreSede = nombreSede; }

    public String getNombreEdificio() { return nombreEdificio; }
    public void setNombreEdificio(String nombreEdificio) { this.nombreEdificio = nombreEdificio; }

    public String getDireccionInterna() { return direccionInterna; }
    public void setDireccionInterna(String direccionInterna) { this.direccionInterna = direccionInterna; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdificacionId that = (EdificacionId) o;
        return Objects.equals(nombreSede, that.nombreSede) &&
               Objects.equals(nombreEdificio, that.nombreEdificio) &&
               Objects.equals(direccionInterna, that.direccionInterna);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombreSede, nombreEdificio, direccionInterna);
    }
}