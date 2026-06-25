package com.ucab.services.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "egresado")
@PrimaryKeyJoinColumns({
    @PrimaryKeyJoinColumn(name = "id_usuario", referencedColumnName = "id_usuario"),
    @PrimaryKeyJoinColumn(name = "fecha_inicio", referencedColumnName = "fecha_inicio")
})
public class Egresado extends PeriodoVinculacion {

    @Column(name = "titulo_obtenido", nullable = false, length = 150)
    private String tituloObtenido;

    @Column(name = "año_graduacion", nullable = false)
    private Integer añoGraduacion;

    @Column(name = "indice_academico_final", nullable = false, precision = 4, scale = 2)
    private BigDecimal indiceAcademicoFinal;

    // --- Getters y Setters ---
    public String getTituloObtenido() { return tituloObtenido; }
    public void setTituloObtenido(String tituloObtenido) { this.tituloObtenido = tituloObtenido; }

    public Integer getAñoGraduacion() { return añoGraduacion; }
    public void setAñoGraduacion(Integer añoGraduacion) { this.añoGraduacion = añoGraduacion; }

    public BigDecimal getIndiceAcademicoFinal() { return indiceAcademicoFinal; }
    public void setIndiceAcademicoFinal(BigDecimal indiceAcademicoFinal) { this.indiceAcademicoFinal = indiceAcademicoFinal; }
}