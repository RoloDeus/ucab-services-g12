package com.ucab.services.repository;

import com.ucab.services.entities.HistorialSesion;
import com.ucab.services.entities.HistorialSesionId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialSesionRepository extends JpaRepository<HistorialSesion, HistorialSesionId> {
    
    // Lista el historial paginado para el Panel de Auditoría de Seguridad
    Page<HistorialSesion> findAll(Pageable pageable);
    
    // Lista el historial específico de un usuario (Por si quiere ver sus propias conexiones)
    Page<HistorialSesion> findByUsuario_IdUsuario(Long idUsuario, Pageable pageable);
}