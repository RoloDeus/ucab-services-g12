package com.ucab.services.repository;

import com.ucab.services.entities.BeneficiarioFamiliar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficiarioFamiliarRepository extends JpaRepository<BeneficiarioFamiliar, Long> {
    
    // Búsqueda simple
    List<BeneficiarioFamiliar> findByUsuario_IdUsuario(Long idUsuario);
    
    // Búsqueda con Paginación y Ordenamiento exigida en rúbrica
    Page<BeneficiarioFamiliar> findByUsuario_IdUsuario(Long idUsuario, Pageable pageable);
}