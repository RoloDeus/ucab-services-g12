package com.ucab.services.controller;

import com.ucab.services.entities.*;
import com.ucab.services.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final BecaRepository becaRepository;
    private final PreparaduriaRepository preparaduriaRepository;
    private final ProfesorRepository profesorRepository;

    public AdminController(UsuarioRepository usuarioRepository, EstudianteRepository estudianteRepository, BecaRepository becaRepository, PreparaduriaRepository preparaduriaRepository, ProfesorRepository profesorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.estudianteRepository = estudianteRepository;
        this.becaRepository = becaRepository;
        this.preparaduriaRepository = preparaduriaRepository;
        this.profesorRepository = profesorRepository;
    }

    // 1. Cargar el Panel Administrativo
    @GetMapping("/panel")
    public String mostrarPanel(@RequestParam String correo, Model model) {
        Optional<Usuario> admin = usuarioRepository.findByCorreoInstitucional(correo);
        if (admin.isEmpty()) return "redirect:/login";
        
        model.addAttribute("admin", admin.get());
        
        // Traemos las cuentas bloqueadas/suspendidas
        List<Usuario> cuentasInactivas = usuarioRepository.findByEstadoCuentaIn(Arrays.asList("Bloqueada", "Suspendida"));
        model.addAttribute("cuentasInactivas", cuentasInactivas);
        
        // Traemos a todos los estudiantes para que el admin pueda editarlos
        List<Estudiante> estudiantes = estudianteRepository.findAll();
        model.addAttribute("estudiantes", estudiantes);
        
        return "panel-admin";
    }

    // 2. Acción: Reactivar Cuenta (Desbloqueo)
    @PostMapping("/activar-cuenta")
    @Transactional
    public String activarCuenta(@RequestParam Long idUsuario, @RequestParam String correoAdmin) {
        Optional<Usuario> oUsuario = usuarioRepository.findById(idUsuario);
        if (oUsuario.isPresent()) {
            Usuario u = oUsuario.get();
            u.setEstadoCuenta("Activa"); // Restaura la cuenta
            u.setConteoIntentosFallidos(0); // Resetea la seguridad lógica
            usuarioRepository.save(u);
        }
        return "redirect:/admin/panel?correo=" + correoAdmin;
    }

    // 3. Acción: Actualizar Expediente Estudiantil
    @PostMapping("/actualizar-estudiante")
    @Transactional
    public String actualizarEstudiante(
            @RequestParam String correoAdmin,
            @RequestParam Long idUsuario,
            @RequestParam String facultad, 
            @RequestParam String escuela,  
            @RequestParam String promedio,
            @RequestParam int uc,
            @RequestParam int semestre,
            @RequestParam String tipoBeca,
            @RequestParam String preparaduria) {
        
        List<Estudiante> vinculaciones = estudianteRepository.findByUsuario_IdUsuario(idUsuario);
        if (!vinculaciones.isEmpty()) {
            Estudiante est = vinculaciones.get(vinculaciones.size() - 1); 
            
            // Guardamos la Facultad y Escuela correctas
            est.setFacultadAdscripcion(facultad);
            est.setEscuelaAdscripcion(escuela);

            est.setPromedioPonderado(new BigDecimal(promedio));
            est.setUcAprobadas(uc);
            est.setSemestreActual(semestre);
            estudianteRepository.save(est);

            // B. Asigna Beca si se seleccionó una
            if (!tipoBeca.equals("Ninguna")) {
                Beca beca = new Beca();
                beca.setEstudiante(est);
                beca.setTipoBeca(tipoBeca);
                beca.setCumplimientoIndice(true);
                beca.setEstatusBeneficio("Activo");
                becaRepository.save(beca);
            }

            // C. Asigna Preparaduría (Tutoría) si se escribió una materia
            if (!preparaduria.trim().isEmpty()) {
                Preparaduria prep = new Preparaduria();
                prep.setEstudiante(est);
                prep.setAsignaturaAsignada(preparaduria);
                prep.setHorasAyudantia(16);
                preparaduriaRepository.save(prep);
            }
        }
        return "redirect:/admin/panel?correo=" + correoAdmin;
    }

    @PostMapping("/profesor/actualizar")
    public String actualizarFichaDocente(@RequestParam String correo, 
                                         @RequestParam String cedulaProfesor,
                                         @RequestParam String cargoDocente, 
                                         @RequestParam(required = false) String codigoInvestigador,
                                         @RequestParam String unidadAdscripcion) {
        try {
            // Lógica de valores por defecto requerida
            String codigoCDCH = (codigoInvestigador == null || codigoInvestigador.trim().isEmpty()) 
                                ? "Por Asignar" : codigoInvestigador.trim();

            profesorRepository.actualizarFichaProfesor(cedulaProfesor, cargoDocente, codigoCDCH, unidadAdscripcion);
            
            return "redirect:/admin/panel?correo=" + correo + "&exito=" + codificarMsj("Ficha del docente actualizada correctamente.");
        } catch (Exception e) {
            return "redirect:/admin/panel?correo=" + correo + "&error=" + codificarMsj("Error al actualizar. Verifique que la cédula pertenezca a un profesor activo.");
        }
    }

    // Helper: codifica mensajes para incluirlos en URLs
    private String codificarMsj(String mensaje) {
        try {
            return URLEncoder.encode(mensaje, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            return mensaje;
        }
    }
}