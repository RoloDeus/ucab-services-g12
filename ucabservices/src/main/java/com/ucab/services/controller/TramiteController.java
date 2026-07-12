package com.ucab.services.controller;
 
import com.ucab.services.services.TramiteService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
import java.util.Map;
 
@RestController
@RequestMapping("/api/tramites")
public class TramiteController {
 
    private final TramiteService servicio;
 
    public TramiteController(TramiteService servicio) {
        this.servicio = servicio;
    }
 
    @GetMapping("/pasos")
    public List<Map<String, Object>> listarPasos() {
        return servicio.listarPasos();
    }
 
    public record CompletarPasoRequest(Long idSolicitud, Long idUsuario, Integer secuenciaPaso) {}
 
    @PostMapping("/completar")
    public ResponseEntity<?> completar(@RequestBody CompletarPasoRequest datos) {
        try {
            servicio.completarPaso(datos.idSolicitud(), datos.idUsuario(), datos.secuenciaPaso());
            return ResponseEntity.ok(Map.of("mensaje", "Paso marcado como completado."));
        } catch (DataAccessException e) {
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", mensajeLimpio(e)));
        }
    }
 
    private String mensajeLimpio(DataAccessException e) {
        Throwable causa = e.getRootCause();
        String texto = (causa != null ? causa.getMessage() : e.getMessage());
        if (texto == null) return "Ocurrió un error al completar el paso.";
        texto = texto.replace("ERROR: ", "");
        int saltoLinea = texto.indexOf('\n');
        return saltoLinea > 0 ? texto.substring(0, saltoLinea) : texto;
    }
}
 
