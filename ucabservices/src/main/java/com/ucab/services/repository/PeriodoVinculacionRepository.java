package com.ucab.services.repository;

import com.ucab.services.entities.PeriodoVinculacion;
import com.ucab.services.entities.PeriodoVinculacionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeriodoVinculacionRepository extends JpaRepository<PeriodoVinculacion, PeriodoVinculacionId> {
    
    // Con este método podremos buscar todo el historial de roles (estudiante, profesor, etc.) de un usuario
    List<PeriodoVinculacion> findByUsuario_IdUsuario(Long idUsuario);
}


