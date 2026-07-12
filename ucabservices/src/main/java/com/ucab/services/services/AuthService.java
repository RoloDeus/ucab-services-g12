package com.ucab.services.services;

import com.ucab.services.entities.Usuario;
import com.ucab.services.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Map;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final TrayectoriaService trayectoriaService;

    public AuthService(UsuarioRepository usuarioRepository, TrayectoriaService trayectoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.trayectoriaService = trayectoriaService;
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
    @Transactional
    public ResponseEntity<?> registrarUsuario(Usuario nuevoUsuario, String rolSeleccionado) {
        try {
            if (usuarioRepository.findByCorreoInstitucional(nuevoUsuario.getCorreoInstitucional()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "El correo ya está en uso."));
            }
            
            // 1. Valores por defecto para el usuario
            nuevoUsuario.setEstadoCuenta("Activa");
            nuevoUsuario.setConteoIntentosFallidos(0);
            nuevoUsuario.setIndiceRecurrencia(0);
            nuevoUsuario.setCategoriaFidelidad("Regular");
            nuevoUsuario.setEstatusVerificacionDosPasos(false);

            // 2. Guardamos el registro base en la tabla 'usuario'
            Usuario guardado = usuarioRepository.save(nuevoUsuario);

            // 3. Evaluamos y asignamos el Periodo de Vinculación y Herencia
            if (rolSeleccionado != null) {
                switch (rolSeleccionado) {
                    case "Estudiante":
                        trayectoriaService.asignarRolEstudiantePrueba(guardado.getCorreoInstitucional());
                        break;
                    case "Profesor":
                        trayectoriaService.asignarRolProfesorPrueba(guardado.getCorreoInstitucional());
                        break;
                    case "Personal Administrativo":
                        // trayectoriaService.asignarRolAdministrativoPrueba(guardado.getCorreoInstitucional());
                        break;
                    case "Egresado":
                        // trayectoriaService.asignarRolEgresadoPrueba(guardado.getCorreoInstitucional());
                        break;
                    default:
                        // Si mandan un rol que no existe en tu BD, se ignora o puedes lanzar un error
                        break;
                }
            }

            return ResponseEntity.ok(Map.of("status", "success", "mensaje", "Registrado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("mensaje", "Error: " + e.getMessage()));
        }
    }
}