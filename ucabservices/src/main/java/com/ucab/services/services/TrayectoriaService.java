package com.ucab.services.services;

import com.ucab.services.entities.Estudiante;
import com.ucab.services.entities.Usuario;
import com.ucab.services.repository.EstudianteRepository;
import com.ucab.services.repository.UsuarioRepository;
import com.ucab.services.repository.ProfesorRepository;
import com.ucab.services.entities.Profesor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class TrayectoriaService {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final ProfesorRepository profesorRepository;

    public TrayectoriaService(UsuarioRepository usuarioRepository, EstudianteRepository estudianteRepository,
                              ProfesorRepository profesorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.estudianteRepository = estudianteRepository;
        this.profesorRepository = profesorRepository;
    }

    @Transactional
    public String asignarRolEstudiantePrueba(String correo) {
        Optional<Usuario> oUsuario = usuarioRepository.findByCorreoInstitucional(correo);
        
        if (oUsuario.isEmpty()) {
            return "Usuario no encontrado en la base de datos.";
        }

        Usuario usuario = oUsuario.get();

        // Creamos la entidad hija (Estudiante)
        Estudiante estudiante = new Estudiante();
        estudiante.setUsuario(usuario);
        estudiante.setFechaInicio(LocalDate.now()); 
        estudiante.setRolInstitucional("Estudiante"); 
        
        // Atributos base "Limpios" (Secretaría los actualizará después)
        estudiante.setPromedioPonderado(new BigDecimal("0.00"));
        estudiante.setUcAprobadas(0);
        estudiante.setSemestreActual(1);
        estudiante.setFacultadAdscripcion("Por Asignar");
        estudiante.setEscuelaAdscripcion("Por Asignar");

        try {
            // Guardamos SOLO al estudiante, sin asignarle preparadurías
            estudianteRepository.save(estudiante);
            
            return "Rol de Estudiante asignado exitosamente al expediente de: " + usuario.getNombres();
        } catch (Exception e) {
            return "Error al asignar rol (verifica la integridad referencial): " + e.getMessage();
        }
    }

    @Transactional
    public String asignarRolProfesorPrueba(String correo) {
        Optional<Usuario> oUsuario = usuarioRepository.findByCorreoInstitucional(correo);
        
        if (oUsuario.isEmpty()) {
            return "Usuario no encontrado.";
        }

        Usuario usuario = oUsuario.get();

        Profesor profesor = new Profesor();
        profesor.setUsuario(usuario);
        profesor.setFechaInicio(LocalDate.now()); 
        profesor.setRolInstitucional("Profesor"); // Atributo Padre
        
        // Atributos de Profesor
        profesor.setUnidadAdscripcionPresupuestaria("Facultad de Ingeniería");
        profesor.setCodigoInvestigador("INV-UCAB-8809");
        profesor.setCargoAdministrativo("Profesor Asociado"); 

        try {
            profesorRepository.save(profesor);
            return "Rol de Profesor asignado a: " + usuario.getNombres();
        } catch (Exception e) {
            return "Error al asignar rol de profesor: " + e.getMessage();
        }
    }
}
