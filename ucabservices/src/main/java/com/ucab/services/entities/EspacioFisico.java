package com.ucab.services.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "espacio_fisico")
@IdClass(EspacioFisicoId.class)
public class EspacioFisico {

    @Id
    @Column(name = "nombre_sede", nullable = false, length = 100)
    private String nombreSede;

    @Id
    @Column(name = "nombre_edificio", nullable = false, length = 100)
    private String nombreEdificio;

    @Id
    @Column(name = "id_espacio", nullable = false, length = 50)
    private String idEspacio;

    @Column(name = "direccion_interna", nullable = false, length = 255)
    private String direccionInterna;

    @Column(name = "capacidad_maxima_aforo", nullable = false)
    private Integer capacidadMaximaAforo;

    @Column(name = "tipo_mobiliario", nullable = false, length = 100)
    private String tipoMobiliario;

    @Column(name = "estado_mantenimiento", nullable = false, length = 50)
    private String estadoMantenimiento = "Operativo";

    @Column(name = "registro_disponibilidad", nullable = false, length = 20)
    private String registroDisponibilidad = "Disponible";

    // --- Getters y Setters ---
    public String getNombreSede() { return nombreSede; }
    public void setNombreSede(String nombreSede) { this.nombreSede = nombreSede; }

    public String getNombreEdificio() { return nombreEdificio; }
    public void setNombreEdificio(String nombreEdificio) { this.nombreEdificio = nombreEdificio; }

    public String getIdEspacio() { return idEspacio; }
    public void setIdEspacio(String idEspacio) { this.idEspacio = idEspacio; }

    public String getDireccionInterna() { return direccionInterna; }
    public void setDireccionInterna(String direccionInterna) { this.direccionInterna = direccionInterna; }

    public Integer getCapacidadMaximaAforo() { return capacidadMaximaAforo; }
    public void setCapacidadMaximaAforo(Integer capacidadMaximaAforo) { this.capacidadMaximaAforo = capacidadMaximaAforo; }

    public String getTipoMobiliario() { return tipoMobiliario; }
    public void setTipoMobiliario(String tipoMobiliario) { this.tipoMobiliario = tipoMobiliario; }

    public String getEstadoMantenimiento() { return estadoMantenimiento; }
    public void setEstadoMantenimiento(String estadoMantenimiento) { this.estadoMantenimiento = estadoMantenimiento; }

    public String getRegistroDisponibilidad() { return registroDisponibilidad; }
    public void setRegistroDisponibilidad(String registroDisponibilidad) { this.registroDisponibilidad = registroDisponibilidad; }
}