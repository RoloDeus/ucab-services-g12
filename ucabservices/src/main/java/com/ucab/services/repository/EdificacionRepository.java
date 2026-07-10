package com.ucab.services.repository;

import com.ucab.services.entities.Edificacion;
import com.ucab.services.entities.EdificacionId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EdificacionRepository extends JpaRepository<Edificacion, EdificacionId> {
    Page<Edificacion> findAll(Pageable pageable);
}