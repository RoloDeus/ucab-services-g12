package com.ucab.services.services;

import com.ucab.services.entities.Usuario;
import com.ucab.services.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Map;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public String login(String correo, String contrasena) {
        Optional<Usuario> oUsuario = usuarioRepository.findByCorreoInstitucional(correo);

        if (oUsuario.isEmpty()) {
            return "Usuario no registrado en UCAB-Services";
        }

        Usuario usuario = oUsuario.get();

        // Control de estados lógicos solicitados en la rúbrica
        if ("Bloqueada".equalsIgnoreCase(usuario.getEstadoCuenta())) {
            return "La cuenta se encuentra bloqueada por exceso de intentos fallidos.";
        }
        if ("Suspendida".equalsIgnoreCase(usuario.getEstadoCuenta())) {
            return "La cuenta se encuentra temporalmente suspendida.";
        }

        // Validación de credenciales
        if (usuario.getContrasena().equals(contrasena)) {
            // LOGIN EXITOSO -> Ejecutamos el Stored Procedure
            usuarioRepository.registrarLoginExitoso(usuario.getCedula());
            return "Login Exitoso";
        } else {
            // LOGIN RECHAZADO -> Disparamos el incremento por BD para gatillar el Trigger trg_seguridad_bloqueo_cuenta
            usuarioRepository.incrementarIntentosFallidos(correo);
            
            int nuevosIntentos = usuario.getConteoIntentosFallidos() + 1;
            
            if (nuevosIntentos >= 3) {
                return "Contraseña incorrecta. Su cuenta ha sido bloqueada por seguridad.";
            }
            return "Contraseña incorrecta. Intentos fallidos: " + nuevosIntentos + "/3";
        }
    }

    // Lógica para Registro de Nuevos Miembros (HU-01)
    public ResponseEntity<?> registrarUsuario(Usuario nuevoUsuario) {
        try {
            // Validaciones iniciales básicas del negocio
            if (usuarioRepository.findByCorreoInstitucional(nuevoUsuario.getCorreoInstitucional()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "El correo institucional ya se encuentra en uso."));
            }
            
            // Inicialización de estados por defecto de acuerdo a las restricciones de la BD
            nuevoUsuario.setEstadoCuenta("Activa");
            nuevoUsuario.setConteoIntentosFallidos(0);
            nuevoUsuario.setIndiceRecurrencia(0);
            nuevoUsuario.setCategoriaFidelidad("Regular");
            nuevoUsuario.setEstatusVerificacionDosPasos(false);

            Usuario guardado = usuarioRepository.save(nuevoUsuario);
            return ResponseEntity.ok(Map.of(
                "status", "success", 
                "mensaje", "Usuario registrado con éxito", 
                "id", guardado.getIdUsuario()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("mensaje", "Error en el servidor al registrar: " + e.getMessage()));
        }
    }
}