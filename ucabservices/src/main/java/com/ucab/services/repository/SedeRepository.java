package com.ucab.services.repository;

import com.ucab.services.entities.Sede;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SedeRepository extends JpaRepository<Sede, String> {
    Page<Sede> findAll(Pageable pageable);
}