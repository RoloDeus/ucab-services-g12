package com.ucab.services.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "beneficiario_familiar")
public class BeneficiarioFamiliar {

    @Id
    @Column(name = "ci_familiar", nullable = false)
    private Long ciFamiliar;

    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "parentesco", nullable = false, length = 20)
    private String parentesco;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "esquema_vacunacion", length = 20)
    private String esquemaVacunacion;

    @Column(name = "centro_educacion_inicial", length = 20)
    private String centroEducacionInicial;

    @Column(name = "constancia_estudios_universitarios", length = 20)
    private String constanciaEstudiosUniversitarios;

    @Column(name = "certificado_solteria", length = 20)
    private String certificadoSolteria;

    // Getters y Setters
    public Long getCiFamiliar() { return ciFamiliar; }
    public void setCiFamiliar(Long ciFamiliar) { this.ciFamiliar = ciFamiliar; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getParentesco() { return parentesco; }
    public void setParentesco(String parentesco) { this.parentesco = parentesco; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getEsquemaVacunacion() { return esquemaVacunacion; }
    public void setEsquemaVacunacion(String esquemaVacunacion) { this.esquemaVacunacion = esquemaVacunacion; }

    public String getCentroEducacionInicial() { return centroEducacionInicial; }
    public void setCentroEducacionInicial(String centroEducacionInicial) { this.centroEducacionInicial = centroEducacionInicial; }

    public String getConstanciaEstudiosUniversitarios() { return constanciaEstudiosUniversitarios; }
    public void setConstanciaEstudiosUniversitarios(String constanciaEstudiosUniversitarios) { this.constanciaEstudiosUniversitarios = constanciaEstudiosUniversitarios; }

    public String getCertificadoSolteria() { return certificadoSolteria; }
    public void setCertificadoSolteria(String certificadoSolteria) { this.certificadoSolteria = certificadoSolteria; }
}