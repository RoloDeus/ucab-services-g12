package com.ucab.services.repository;

import com.ucab.services.entities.Beca;
import com.ucab.services.entities.BecaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BecaRepository extends JpaRepository<Beca, BecaId> {
    List<Beca> findByEstudiante_Usuario_IdUsuario(Long idUsuario);
}