package com.ucab.services.repository;

import com.ucab.services.entities.AcompananteTemporal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcompananteTemporalRepository extends JpaRepository<AcompananteTemporal, Long> {
    
    // Búsqueda paginada basada en el ID del usuario titular
    Page<AcompananteTemporal> findByUsuario_IdUsuario(Long idUsuario, Pageable pageable);
}