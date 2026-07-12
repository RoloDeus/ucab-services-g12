package com.ucab.services.entities;

import java.io.Serializable;
import java.util.Objects;

public class EspacioFisicoId implements Serializable {
    
    private String nombreSede;
    private String nombreEdificio;
    private String idEspacio;

    public EspacioFisicoId() {}

    public EspacioFisicoId(String nombreSede, String nombreEdificio, String idEspacio) {
        this.nombreSede = nombreSede;
        this.nombreEdificio = nombreEdificio;
        this.idEspacio = idEspacio;
    }

    // Getters y Setters
    public String getNombreSede() { return nombreSede; }
    public void setNombreSede(String nombreSede) { this.nombreSede = nombreSede; }

    public String getNombreEdificio() { return nombreEdificio; }
    public void setNombreEdificio(String nombreEdificio) { this.nombreEdificio = nombreEdificio; }

    public String getIdEspacio() { return idEspacio; }
    public void setIdEspacio(String idEspacio) { this.idEspacio = idEspacio; }

    // Equals y HashCode (Obligatorios para llaves compuestas en JPA)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EspacioFisicoId that = (EspacioFisicoId) o;
        return Objects.equals(nombreSede, that.nombreSede) &&
               Objects.equals(nombreEdificio, that.nombreEdificio) &&
               Objects.equals(idEspacio, that.idEspacio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombreSede, nombreEdificio, idEspacio);
    }
}