package com.ucab.services.repository;

import com.ucab.services.entities.PeriodoVinculacionId;
import com.ucab.services.entities.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfesorRepository extends JpaRepository<Profesor, PeriodoVinculacionId> {
    List<Profesor> findByUsuario_IdUsuario(Long idUsuario);
}