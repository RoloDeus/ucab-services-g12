package com.ucab.services.repository;
 
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
 
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.SQLWarning;
import java.sql.Statement;
 
@Repository
public class ProcesoRepository {
 
    private final JdbcTemplate jdbcTemplate;
 
    public ProcesoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
 
    // Ejecuta un procedimiento sin parámetros y devuelve
    // el texto de sus avisos, que es donde cada procedimiento
    // cuenta qué hizo
    public String ejecutar(String nombreProcedimiento) {
        return jdbcTemplate.execute((ConnectionCallback<String>) con -> {
            try (Statement st = con.createStatement()) {
                st.execute("CALL " + nombreProcedimiento + "()");
                return leerAvisos(st.getWarnings());
            }
        });
    }
 
    // se necesita un parámetro, la tasa del día.
    public String sincronizarTasaBcv(Double tasa) {
        return jdbcTemplate.execute((ConnectionCallback<String>) con -> {
            try (CallableStatement cs = con.prepareCall("CALL sp_sincronizar_tasa_bcv(?)")) {
                cs.setBigDecimal(1, BigDecimal.valueOf(tasa));
                cs.execute();
                return leerAvisos(cs.getWarnings());
            }
        });
    }
 
    private String leerAvisos(SQLWarning aviso) {
        StringBuilder sb = new StringBuilder();
        while (aviso != null) {
            sb.append(aviso.getMessage()).append(" ");
            aviso = aviso.getNextWarning();
        }
        String texto = sb.toString().trim();
        return texto.isEmpty() ? "Proceso ejecutado correctamente." : texto;
    }
}
 
