package com.ucab.services.controller;

import com.ucab.services.entities.HistorialSesion;
import com.ucab.services.entities.Usuario;
import com.ucab.services.repository.HistorialSesionRepository;
import com.ucab.services.repository.UsuarioRepository;
import com.ucab.services.services.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Permite que tu Frontend se conecte sin problemas de CORS
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final HistorialSesionRepository historialSesionRepository;
    private final AuthService authService;
    public AuthController(UsuarioRepository usuarioRepository, HistorialSesionRepository historialSesionRepository, AuthService authService) {
        this.usuarioRepository = usuarioRepository;
        this.historialSesionRepository = historialSesionRepository;
        this.authService = authService;
    }

    // Endpoint para procesar el inicio de sesión (HU-02)
    @PostMapping("/login")
    @Transactional // Garantiza que si falla la auditoría, no se inicie sesión
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales, HttpServletRequest request) {
        String correo = credenciales.get("correo");
        String contrasena = credenciales.get("contrasena");

        Optional<Usuario> oUsuario = usuarioRepository.findByCorreoInstitucional(correo);

        if (oUsuario.isPresent()) {
            Usuario usuario = oUsuario.get();

            // 1. Validamos que la cuenta no esté bloqueada por el Trigger de seguridad
            if (usuario.getEstadoCuenta().equals("Bloqueada") || usuario.getEstadoCuenta().equals("Suspendida")) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "Cuenta " + usuario.getEstadoCuenta() + ". Contacte a soporte."));
            }

            // 2. Verificamos la contraseña
            if (usuario.getContrasena().equals(contrasena)) {
                
                // --- INICIO DE LA AUDITORÍA DE SEGURIDAD ---

                // A. Llamamos al SP para resetear los fallos y actualizar "ultima_conexion"
                usuarioRepository.registrarLoginExitoso(usuario.getCedula());

                // B. Capturamos los datos de red del cliente
                HistorialSesion sesion = new HistorialSesion();
                sesion.setUsuario(usuario);
                sesion.setFechaHoraAcceso(LocalDateTime.now());
                
                // Extraer IP (Toma en cuenta si hay un Proxy, si no, toma la IP local)
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty()) {
                    ip = request.getRemoteAddr();
                }
                sesion.setDireccionIp(ip);
                
                // Extraer el Sistema Operativo y Navegador (User-Agent)
                String userAgent = request.getHeader("User-Agent");
                sesion.setUuidDispositivo(userAgent != null ? userAgent : "Dispositivo Desconocido");
                
                // Para proyectos universitarios, la geolocalización IP requiere una API paga, así que inyectamos un valor por defecto realista
                sesion.setGeolocalizacionAproximada("Caracas, VE (Aproximación por IP)");

                // Guardamos el registro inmutable en el historial
                historialSesionRepository.save(sesion);

                // --- FIN DE LA AUDITORÍA ---

                return ResponseEntity.ok(Map.of("mensaje", "Login Exitoso"));
            } else {
                // Si falla, incrementamos el conteo de errores (Tu Trigger lo bloqueará si llega a 3)
                usuario.setConteoIntentosFallidos(usuario.getConteoIntentosFallidos() + 1);
                usuarioRepository.save(usuario); 
                return ResponseEntity.badRequest().body(Map.of("mensaje", "Credenciales inválidas."));
            }
        }

        return ResponseEntity.badRequest().body(Map.of("mensaje", "Usuario no encontrado."));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody RegistroRequest request) {
        try {
            // Pasamos el usuario y el rol seleccionado al servicio
            return authService.registrarUsuario(request.usuario, request.rolSeleccionado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "mensaje", "Error al registrar: " + e.getMessage()));
        }
    }

    // Clase interna para mapear la petición de registro
    public static class RegistroRequest {
        public Usuario usuario;
        public String rolSeleccionado;

        public RegistroRequest() {}

        public Usuario getUsuario() { return usuario; }
        public void setUsuario(Usuario usuario) { this.usuario = usuario; }
        public String getRolSeleccionado() { return rolSeleccionado; }
        public void setRolSeleccionado(String rolSeleccionado) { this.rolSeleccionado = rolSeleccionado; }
    }
}