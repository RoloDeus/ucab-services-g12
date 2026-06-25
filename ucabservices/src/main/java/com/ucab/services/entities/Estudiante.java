package com.ucab.services.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "estudiante")
@PrimaryKeyJoinColumns({
    @PrimaryKeyJoinColumn(name = "id_usuario", referencedColumnName = "id_usuario"),
    @PrimaryKeyJoinColumn(name = "fecha_inicio", referencedColumnName = "fecha_inicio")
})
public class Estudiante extends PeriodoVinculacion {

    @Column(name = "promedio_ponderado", nullable = false, precision = 4, scale = 2)
    private BigDecimal promedioPonderado;

    @Column(name = "uc_aprobadas", nullable = false)
    private Integer ucAprobadas = 0;

    @Column(name = "semestre_actual", nullable = false)
    private Integer semestreActual;

    @Column(name = "facultad_adscripcion", nullable = false, length = 100)
    private String facultadAdscripcion;

    @Column(name = "escuela_adscripcion", nullable = false, length = 100)
    private String escuelaAdscripcion;

    // Getters y Setters
    public BigDecimal getPromedioPonderado() { return promedioPonderado; }
    public void setPromedioPonderado(BigDecimal promedioPonderado) { this.promedioPonderado = promedioPonderado; }

    public Integer getUcAprobadas() { return ucAprobadas; }
    public void setUcAprobadas(Integer ucAprobadas) { this.ucAprobadas = ucAprobadas; }

    public Integer getSemestreActual() { return semestreActual; }
    public void setSemestreActual(Integer semestreActual) { this.semestreActual = semestreActual; }

    public String getFacultadAdscripcion() { return facultadAdscripcion; }
    public void setFacultadAdscripcion(String facultadAdscripcion) { this.facultadAdscripcion = facultadAdscripcion; }

    public String getEscuelaAdscripcion() { return escuelaAdscripcion; }
    public void setEscuelaAdscripcion(String escuelaAdscripcion) { this.escuelaAdscripcion = escuelaAdscripcion; }
}
