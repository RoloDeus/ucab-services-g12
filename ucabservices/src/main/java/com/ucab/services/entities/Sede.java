package com.ucab.services.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sede")
public class Sede {

    @Id
    @Column(name = "nombre_sede", nullable = false, length = 100)
    private String nombreSede;

    public Sede() {}

    public Sede(String nombreSede) {
        this.nombreSede = nombreSede;
    }

    public String getNombreSede() { return nombreSede; }
    public void setNombreSede(String nombreSede) { this.nombreSede = nombreSede; }
}