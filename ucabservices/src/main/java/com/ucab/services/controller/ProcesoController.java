package com.ucab.services.controller;
 
import com.ucab.services.services.ProcesoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.Map;
 
@RestController
@RequestMapping("/api/procesos")
public class ProcesoController {
 
    private final ProcesoService servicio;
 
    public ProcesoController(ProcesoService servicio) {
        this.servicio = servicio;
    }
 
    @PostMapping("/{clave}")
    public ResponseEntity<?> ejecutar(@PathVariable String clave) {
        try {
            String mensaje = servicio.ejecutarProceso(clave);
            return ResponseEntity.ok(Map.of("mensaje", mensaje));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
 
    public record TasaBcvRequest(Double tasa) {}
 
    @PostMapping("/tasa-bcv")
    public ResponseEntity<?> sincronizarTasa(@RequestBody TasaBcvRequest datos) {
        try {
            String mensaje = servicio.sincronizarTasaBcv(datos.tasa());
            return ResponseEntity.ok(Map.of("mensaje", mensaje));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
 
