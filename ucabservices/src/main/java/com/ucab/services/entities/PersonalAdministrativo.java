package com.ucab.services.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "personal_administrativo")
@PrimaryKeyJoinColumns({
    @PrimaryKeyJoinColumn(name = "id_usuario", referencedColumnName = "id_usuario"),
    @PrimaryKeyJoinColumn(name = "fecha_inicio", referencedColumnName = "fecha_inicio")
})
public class PersonalAdministrativo extends PeriodoVinculacion {

    @Column(name = "cargo_administrativo", nullable = false, length = 150)
    private String cargoAdministrativo;

    @Column(name = "unidad_adscripcion_presupuestaria", nullable = false, length = 150)
    private String unidadAdscripcionPresupuestaria;

    @Column(name = "carga_horaria_semanal", nullable = false)
    private Integer cargaHorariaSemanal;

    // --- Getters y Setters ---
    public String getCargoAdministrativo() { return cargoAdministrativo; }
    public void setCargoAdministrativo(String cargoAdministrativo) { this.cargoAdministrativo = cargoAdministrativo; }

    public String getUnidadAdscripcionPresupuestaria() { return unidadAdscripcionPresupuestaria; }
    public void setUnidadAdscripcionPresupuestaria(String unidadAdscripcionPresupuestaria) { this.unidadAdscripcionPresupuestaria = unidadAdscripcionPresupuestaria; }

    public Integer getCargaHorariaSemanal() { return cargaHorariaSemanal; }
    public void setCargaHorariaSemanal(Integer cargaHorariaSemanal) { this.cargaHorariaSemanal = cargaHorariaSemanal; }
}