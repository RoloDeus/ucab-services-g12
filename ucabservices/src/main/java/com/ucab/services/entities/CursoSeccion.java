package com.ucab.services.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "curso_seccion")
public class CursoSeccion {

    @Id
    @Column(name = "codigo_curso", nullable = false, length = 50)
    private String codigoCurso;

    @Column(name = "nombre_materia", nullable = false, length = 150)
    private String nombreMateria;

    public CursoSeccion() {}

    public String getCodigoCurso() { return codigoCurso; }
    public void setCodigoCurso(String codigoCurso) { this.codigoCurso = codigoCurso; }

    public String getNombreMateria() { return nombreMateria; }
    public void setNombreMateria(String nombreMateria) { this.nombreMateria = nombreMateria; }
}