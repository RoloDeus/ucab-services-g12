package com.ucab.services.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "edificacion")
@IdClass(EdificacionId.class)
public class Edificacion {

    @Id
    @Column(name = "nombre_sede", nullable = false, length = 100)
    private String nombreSede;

    @Id
    @Column(name = "nombre_edificio", nullable = false, length = 100)
    private String nombreEdificio;

    @Id
    @Column(name = "direccion_interna", nullable = false, length = 255)
    private String direccionInterna;

    // --- Getters y Setters ---
    public String getNombreSede() { return nombreSede; }
    public void setNombreSede(String nombreSede) { this.nombreSede = nombreSede; }

    public String getNombreEdificio() { return nombreEdificio; }
    public void setNombreEdificio(String nombreEdificio) { this.nombreEdificio = nombreEdificio; }

    public String getDireccionInterna() { return direccionInterna; }
    public void setDireccionInterna(String direccionInterna) { this.direccionInterna = direccionInterna; }
}