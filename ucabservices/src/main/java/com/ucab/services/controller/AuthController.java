package com.ucab.services.controller;

import com.ucab.services.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Permite que tu Frontend se conecte sin problemas de CORS
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Endpoint para procesar el inicio de sesión (HU-02)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String correo = loginRequest.get("correo");
        String contrasena = loginRequest.get("contrasena");

        if (correo == null || contrasena == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Correo y contraseña son obligatorios."));
        }

        String resultado = authService.login(correo, contrasena);

        if ("Login Exitoso".equals(resultado)) {
            return ResponseEntity.ok(Map.of("status", "success", "mensaje", resultado));
        } else {
            return ResponseEntity.status(401).body(Map.of("status", "error", "mensaje", resultado));
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody com.ucab.services.entities.Usuario nuevoUsuario) {
        return authService.registrarUsuario(nuevoUsuario);
    }
}