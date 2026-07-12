package com.ucab.services.repository;

import com.ucab.services.entities.Estudiante;
import com.ucab.services.entities.PeriodoVinculacionId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EstudianteRepository extends JpaRepository<Estudiante, PeriodoVinculacionId> {
    
    // Spring Boot construirá la consulta SQL automáticamente basándose en este nombre
    List<Estudiante> findByUsuario_IdUsuario(Long idUsuario);
}