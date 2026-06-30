package com.ucab.services.controller;

import com.ucab.services.services.TrayectoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/trayectoria")
@CrossOrigin(origins = "*")
public class TrayectoriaController {

    private final TrayectoriaService trayectoriaService;

    public TrayectoriaController(TrayectoriaService trayectoriaService) {
        this.trayectoriaService = trayectoriaService;
    }

    // Endpoint para inyectar los datos de prueba
    @PostMapping("/asignar-estudiante")
    public ResponseEntity<?> asignarEstudiante(@RequestBody Map<String, String> request) {
        String correo = request.get("correo");
        
        if (correo == null || correo.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El correo es obligatorio"));
        }

        String resultado = trayectoriaService.asignarRolEstudiantePrueba(correo);
        return ResponseEntity.ok(Map.of("mensaje", resultado));
    }

    @PostMapping("/asignar-profesor")
    public ResponseEntity<?> asignarProfesor(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(Map.of("mensaje", trayectoriaService.asignarRolProfesorPrueba(request.get("correo"))));
    }
}
