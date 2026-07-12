package com.ucab.services.repository;

import com.ucab.services.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Para buscar al iniciar sesión (HU-02)
    Optional<Usuario> findByCorreoInstitucional(String correoInstitucional);

    // Llamada nativa al procedimiento almacenado de inicio exitoso
    @Modifying
    @Transactional
    @Query(value = "CALL sp_registrar_login_exitoso(:cedula)", nativeQuery = true)
    void registrarLoginExitoso(@Param("cedula") String cedula);

    // Consulta nativa para incrementar intentos y dejar que el Trigger actúe sin bloqueos de JPA
    @Modifying
    @Transactional
    @Query(value = "UPDATE usuario SET conteo_intentos_fallidos = conteo_intentos_fallidos + 1 WHERE correo_institucional = :correo", nativeQuery = true)
    void incrementarIntentosFallidos(@Param("correo") String correo);

    @Transactional
    @Modifying
    @Query(value = "CALL sp_solicitar_beca(:cedula, :tipoBeca)", nativeQuery = true)
    void solicitarBeca(@Param("cedula") String cedula, @Param("tipoBeca") String tipoBeca);

    List<Usuario> findByEstadoCuentaIn(List<String> estados);

}