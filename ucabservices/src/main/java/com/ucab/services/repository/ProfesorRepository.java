package com.ucab.services.repository;

import com.ucab.services.entities.PeriodoVinculacionId;
import com.ucab.services.entities.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface ProfesorRepository extends JpaRepository<Profesor, PeriodoVinculacionId> {
    List<Profesor> findByUsuario_IdUsuario(Long idUsuario);

    @Transactional
    @Modifying
    @Query(value = "UPDATE profesor p SET cargo_administrativo = :cargo, codigo_investigador = :codigoInv, unidad_adscripcion_presupuestaria = :unidad " +
                   "FROM usuario u " +
                   "WHERE p.id_usuario = u.id_usuario AND u.cedula = :cedula " +
                   "AND p.fecha_inicio = (SELECT MAX(fecha_inicio) FROM profesor WHERE id_usuario = u.id_usuario)", nativeQuery = true)
    void actualizarFichaProfesor(@Param("cedula") String cedula, @Param("cargo") String cargo, @Param("codigoInv") String codigoInv, @Param("unidad") String unidad);
}