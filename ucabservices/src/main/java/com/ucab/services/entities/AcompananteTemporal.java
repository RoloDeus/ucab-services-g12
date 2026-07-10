package com.ucab.services.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "acompanante_temporal")
public class AcompananteTemporal {

    @Id
    @Column(name = "ci_acompanante", nullable = false)
    private Long ciAcompanante;

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDate fechaExpiracion;

    // --- Getters y Setters ---
    public Long getCiAcompanante() { return ciAcompanante; }
    public void setCiAcompanante(Long ciAcompanante) { this.ciAcompanante = ciAcompanante; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public LocalDate getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDate fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
}