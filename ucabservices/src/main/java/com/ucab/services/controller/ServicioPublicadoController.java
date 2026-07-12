package com.ucab.services.controller;
 
import com.ucab.services.services.ServicioPublicadoService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
import java.util.Map;
 
@RestController
@RequestMapping("/api/servicios")
public class ServicioPublicadoController {
 
    private final ServicioPublicadoService servicio;
 
    public ServicioPublicadoController(ServicioPublicadoService servicio) {
        this.servicio = servicio;
    }
 
    @GetMapping
    public List<Map<String, Object>> listar() {
        return servicio.listarTodos();
    }
 
    @GetMapping("/categorias")
    public List<Map<String, Object>> categorias() {
        return servicio.listarCategorias();
    }
 
    @GetMapping("/sedes")
    public List<Map<String, Object>> sedes() {
        return servicio.listarSedes();
    }
 
    @GetMapping("/entidades")
    public List<Map<String, Object>> entidades() {
        return servicio.listarEntidades();
    }
 
    // Los datos que llegan del formulario HTML 
    public record NuevoServicio(String categoria, Long idEntidad, String sede,
                                 String descripcion, Double precioBase, Double ajuste) {}
 
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody NuevoServicio datos) {
        try {
            servicio.crear(datos.categoria(), datos.idEntidad(), datos.sede(),
                            datos.descripcion(), datos.precioBase(), datos.ajuste());
            return ResponseEntity.ok(Map.of("mensaje", "Servicio publicado correctamente."));
        } catch (DataAccessException e) {
            // Aquí es donde esta el error del trigger de HU-61 si el precio
            // supera el tope. Lo limpiamos para no mostrar el stacktrace crudo.
            // (esto se traslada a un GlobalExceptionHandler compartido.)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", mensajeLimpio(e)));
        }
    }
 
    private String mensajeLimpio(DataAccessException e) {
        Throwable causa = e.getRootCause();
        String texto = (causa != null ? causa.getMessage() : e.getMessage());
        if (texto == null) return "Ocurrió un error al guardar el servicio.";
        texto = texto.replace("ERROR: ", "");
        int saltoLinea = texto.indexOf('\n');
        return saltoLinea > 0 ? texto.substring(0, saltoLinea) : texto;
    }
}
 
