package com.ucab.services.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "periodo_vinculacion")
@IdClass(PeriodoVinculacionId.class) // Indicamos la clase de la llave compuesta
@Inheritance(strategy = InheritanceType.JOINED) // Estrategia de herencia para tablas separadas
public class PeriodoVinculacion {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    private Usuario usuario;

    @Id
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "rol_institucional", nullable = false, length = 30)
    private String rolInstitucional;

    @Column(name = "fecha_finalizacion")
    private LocalDate fechaFinalizacion;

    // Getters y Setters
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getRolInstitucional() { return rolInstitucional; }
    public void setRolInstitucional(String rolInstitucional) { this.rolInstitucional = rolInstitucional; }

    public LocalDate getFechaFinalizacion() { return fechaFinalizacion; }
    public void setFechaFinalizacion(LocalDate fechaFinalizacion) { this.fechaFinalizacion = fechaFinalizacion; }
}