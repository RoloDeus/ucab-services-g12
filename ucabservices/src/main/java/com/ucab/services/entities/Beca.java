package com.ucab.services.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "beca")
@IdClass(BecaId.class)
public class Beca {

    @Id
    @OneToOne
    @JoinColumns({
        @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario"),
        @JoinColumn(name = "fecha_inicio", referencedColumnName = "fecha_inicio")
    })
    private Estudiante estudiante;

    @Column(name = "tipo_beca", nullable = false, length = 50)
    private String tipoBeca;

    @Column(name = "estatus_beneficio", nullable = false, length = 20)
    private String estatusBeneficio = "Activo";

    @Column(name = "cumplimiento_indice", nullable = false)
    private Boolean cumplimientoIndice;

    // Getters y Setters
    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public String getTipoBeca() { return tipoBeca; }
    public void setTipoBeca(String tipoBeca) { this.tipoBeca = tipoBeca; }

    public String getEstatusBeneficio() { return estatusBeneficio; }
    public void setEstatusBeneficio(String estatusBeneficio) { this.estatusBeneficio = estatusBeneficio; }

    public Boolean getCumplimientoIndice() { return cumplimientoIndice; }
    public void setCumplimientoIndice(Boolean cumplimientoIndice) { this.cumplimientoIndice = cumplimientoIndice; }
}