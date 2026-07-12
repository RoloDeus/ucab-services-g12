package com.ucab.services.repository;
 
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
 
import java.util.List;
import java.util.Map;
 
@Repository
public class ReporteRepository {
 
    private final JdbcTemplate jdbcTemplate;
 
    public ReporteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
 
    public List<Map<String, Object>> obtenerRep01() {
        return jdbcTemplate.queryForList("SELECT * FROM vista_rep01_cuellos_por_oficina");
    }
 
    public List<Map<String, Object>> obtenerRep02() {
        return jdbcTemplate.queryForList("SELECT * FROM vista_rep02_conciliacion");
    }
 
    public List<Map<String, Object>> obtenerRep03() {
        return jdbcTemplate.queryForList("SELECT * FROM vista_rep03_rentabilidad_ocupacion");
    }
 
    public List<Map<String, Object>> obtenerRep04() {
        return jdbcTemplate.queryForList("SELECT * FROM vista_rep04_demografia_beneficiarios");
    }
 
    public List<Map<String, Object>> obtenerRep05() {
        return jdbcTemplate.queryForList("SELECT * FROM vista_rep05_insercion_por_empresa");
    }
 
    public List<Map<String, Object>> obtenerRep06() {
        return jdbcTemplate.queryForList("SELECT * FROM vista_rep06_auditoria_seguridad");
    }
 
    public List<Map<String, Object>> obtenerRep07() {
        return jdbcTemplate.queryForList("SELECT * FROM vista_rep07_estados_cuenta");
    }
 
    public List<Map<String, Object>> obtenerRep08() {
        return jdbcTemplate.queryForList("SELECT * FROM vista_rep08_perfil_recurrencia");
    }
}