package com.ucab.services.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "preparaduria")
@IdClass(PreparaduriaId.class)
public class Preparaduria {

    @Id
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario"),
        @JoinColumn(name = "fecha_inicio", referencedColumnName = "fecha_inicio")
    })
    private Estudiante estudiante;

    @Id
    @Column(name = "asignatura_asignada", length = 150)
    private String asignaturaAsignada;

    @Column(name = "horas_ayudantia", nullable = false)
    private Integer horasAyudantia;

    // Getters y Setters
    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public String getAsignaturaAsignada() { return asignaturaAsignada; }
    public void setAsignaturaAsignada(String asignaturaAsignada) { this.asignaturaAsignada = asignaturaAsignada; }

    public Integer getHorasAyudantia() { return horasAyudantia; }
    public void setHorasAyudantia(Integer horasAyudantia) { this.horasAyudantia = horasAyudantia; }
}