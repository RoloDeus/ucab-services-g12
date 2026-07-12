package com.ucab.services.repository;

import com.ucab.services.entities.EspacioFisico;
import com.ucab.services.entities.EspacioFisicoId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EspacioFisicoRepository extends JpaRepository<EspacioFisico, EspacioFisicoId> {
    
    Page<EspacioFisico> findAll(Pageable pageable);
    
    Page<EspacioFisico> findByNombreSede(String nombreSede, Pageable pageable);

    // NUEVO: Consultas Nativas de Inserción Inteligente
    @Modifying
    @Query(value = "INSERT INTO sede (nombre_sede) VALUES (:sede) ON CONFLICT DO NOTHING", nativeQuery = true)
    void asegurarSedeExistente(@Param("sede") String sede);

    @Modifying
    @Query(value = "INSERT INTO edificacion (nombre_sede, nombre_edificio, direccion_interna) VALUES (:sede, :edificio, :direccion) ON CONFLICT DO NOTHING", nativeQuery = true)
    void asegurarEdificacionExistente(@Param("sede") String sede, @Param("edificio") String edificio, @Param("direccion") String direccion);
}