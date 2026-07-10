package com.ucab.services.repository;

import com.ucab.services.entities.BeneficiarioFamiliar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BeneficiarioFamiliarRepository extends JpaRepository<BeneficiarioFamiliar, Long> {
    
    // Lista todos los familiares de un usuario sin paginar (útil para lógica interna)
    List<BeneficiarioFamiliar> findByUsuario_IdUsuario(Long idUsuario);
    
    // Lista los familiares de un usuario aplicando Paginación y Ordenamiento (exigencia de la rúbrica)
    Page<BeneficiarioFamiliar> findByUsuario_IdUsuario(Long idUsuario, Pageable pageable);

    @Modifying
    @Query(value = "CALL sp_transicion_mayoria_edad()", nativeQuery = true)
    void ejecutarProcedimientoMayoriaEdad();
}