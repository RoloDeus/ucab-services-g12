package com.ucab.services.repository;
 
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
 
import java.util.List;
import java.util.Map;
 
@Repository
public class ServicioPublicadoRepository {
 
    private final JdbcTemplate jdbcTemplate;
 
    public ServicioPublicadoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
 
    // Lista los servicios ya publicados, para la tabla de abajo en la pantalla.
    public List<Map<String, Object>> listarTodos() {
        return jdbcTemplate.queryForList(
            "SELECT id_servicio, nombre_categoria, nombre_sede, descripcion_detallada, " +
            "       precio_base_institucional, ajuste_ubicacion " +
            "FROM servicio_publicado ORDER BY id_servicio DESC");
    }
 
    // Para llenar el <select> de categorías.
    public List<Map<String, Object>> listarCategorias() {
        return jdbcTemplate.queryForList(
            "SELECT nombre_categoria FROM categoria_servicio ORDER BY nombre_categoria");
    }
 
    // Para llenar el <select> de sedes.
    public List<Map<String, Object>> listarSedes() {
        return jdbcTemplate.queryForList(
            "SELECT nombre_sede FROM sede ORDER BY nombre_sede");
    }
 
    // Para llenar el <select> de entidades prestadoras (junta internas y externas
    // en una sola lista legible, con su nombre real en vez de solo el id).
    public List<Map<String, Object>> listarEntidades() {
        return jdbcTemplate.queryForList(
            "SELECT ep.id_entidad, " +
            "       COALESCE(pi.director_oficina || ' (Interno)', ae.razon_social || ' (Externo)') AS nombre_entidad " +
            "FROM entidad_prestadora ep " +
            "LEFT JOIN prestador_interno pi ON pi.id_entidad = ep.id_entidad " +
            "LEFT JOIN aliado_externo ae ON ae.id_entidad = ep.id_entidad " +
            "ORDER BY ep.id_entidad");
    }
 
    // Inserta el nuevo servicio. Si el precio supera el tope de la sede,
    // el trigger trg_validar_tarifa lanza un error y esta línea no llega a completarse.
    public void crear(String categoria, Long idEntidad, String sede, String descripcion,
                       Double precioBase, Double ajuste) {
        jdbcTemplate.update(
            "INSERT INTO servicio_publicado " +
            "(nombre_categoria, id_entidad, nombre_sede, descripcion_detallada, " +
            " precio_base_institucional, ajuste_ubicacion) " +
            "VALUES (?, ?, ?, ?, ?, ?)",
            categoria, idEntidad, sede, descripcion, precioBase, ajuste);
    }
}
 