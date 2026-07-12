package com.ucab.services.repository;

import com.ucab.services.entities.Preparaduria;
import com.ucab.services.entities.PreparaduriaId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreparaduriaRepository extends JpaRepository<Preparaduria, PreparaduriaId> {
    
    // Búsqueda simple (la que ya usas en el Perfil)
    List<Preparaduria> findByEstudiante_Usuario_IdUsuario(Long idUsuario);

    // NUEVO: Búsqueda con Paginación y Ordenamiento
    Page<Preparaduria> findByEstudiante_Usuario_IdUsuario(Long idUsuario, Pageable pageable);
}