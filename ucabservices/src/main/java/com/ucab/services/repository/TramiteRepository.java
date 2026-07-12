package com.ucab.services.repository;
 
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
 
import java.util.List;
import java.util.Map;
 
@Repository
public class TramiteRepository {
 
    private final JdbcTemplate jdbcTemplate;
 
    public TramiteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
 
    public List<Map<String, Object>> listarPasos() {
        return jdbcTemplate.queryForList(
            "SELECT pa.id_solicitud, pa.id_usuario, pa.secuencia_paso, pa.oficina_responsable, " +
            "       pa.estado_paso, pa.fecha_hora_finalizacion_exacta, " +
            "       u.nombres || ' ' || u.apellidos AS titular " +
            "FROM paso_actividad pa " +
            "JOIN usuario u ON u.id_usuario = pa.id_usuario " +
            "ORDER BY pa.id_solicitud, pa.secuencia_paso");
    }
 
    // No tocamos fecha_hora_finalizacion_exacta aquí: el trigger
    // fn_grabar_hora_finalizacion la llena solo al ver estado 'Completado'.
    public void completarPaso(Long idSolicitud, Long idUsuario, Integer secuenciaPaso) {
        jdbcTemplate.update(
            "UPDATE paso_actividad SET estado_paso = 'Completado' " +
            "WHERE id_solicitud = ? AND id_usuario = ? AND secuencia_paso = ?",
            idSolicitud, idUsuario, secuenciaPaso);
    }
}
