package com.ucab.services.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "profesor")
@PrimaryKeyJoinColumns({
    @PrimaryKeyJoinColumn(name = "id_usuario", referencedColumnName = "id_usuario"),
    @PrimaryKeyJoinColumn(name = "fecha_inicio", referencedColumnName = "fecha_inicio")
})
public class Profesor extends PeriodoVinculacion {

    @Column(name = "unidad_adscripcion_presupuestaria", nullable = false, length = 150)
    private String unidadAdscripcionPresupuestaria;

    @Column(name = "codigo_investigador", length = 50)
    private String codigoInvestigador; // Es opcional según tu script

    @Column(name = "cargo_administrativo", nullable = false, length = 100)
    private String cargoAdministrativo;

    // --- Getters y Setters ---
    public String getUnidadAdscripcionPresupuestaria() { return unidadAdscripcionPresupuestaria; }
    public void setUnidadAdscripcionPresupuestaria(String unidadAdscripcionPresupuestaria) { this.unidadAdscripcionPresupuestaria = unidadAdscripcionPresupuestaria; }

    public String getCodigoInvestigador() { return codigoInvestigador; }
    public void setCodigoInvestigador(String codigoInvestigador) { this.codigoInvestigador = codigoInvestigador; }

    public String getCargoAdministrativo() { return cargoAdministrativo; }
    public void setCargoAdministrativo(String cargoAdministrativo) { this.cargoAdministrativo = cargoAdministrativo; }
}