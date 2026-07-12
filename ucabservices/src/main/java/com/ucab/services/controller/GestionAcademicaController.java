package com.ucab.services.controller;

import com.ucab.services.entities.CursoSeccion;
import com.ucab.services.entities.Usuario;
import com.ucab.services.repository.CursoSeccionRepository;
import com.ucab.services.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/academico")
public class GestionAcademicaController {

    private final UsuarioRepository usuarioRepository;
    private final CursoSeccionRepository cursoSeccionRepository;

    public GestionAcademicaController(UsuarioRepository usuarioRepository, CursoSeccionRepository cursoSeccionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.cursoSeccionRepository = cursoSeccionRepository;
    }

    // Pantalla Principal de Secretaría
    @GetMapping
    public String verPanelAcademico(@RequestParam String correo, Model model) {
        Optional<Usuario> oAdmin = usuarioRepository.findByCorreoInstitucional(correo);
        if (oAdmin.isEmpty()) return "redirect:/login";

        model.addAttribute("admin", oAdmin.get());
        model.addAttribute("correo", correo);
        
        // Pasamos la lista de todos los cursos para llenar los desplegables de inscripción
        List<CursoSeccion> listaCursos = cursoSeccionRepository.findAll();
        model.addAttribute("listaCursos", listaCursos);

        return "gestion-academica";
    }

    // 1. Guardar nueva materia
    @PostMapping("/curso/guardar")
    public String registrarCurso(@RequestParam String correo, @RequestParam String codigoCurso, @RequestParam String nombreMateria) {
        try {
            cursoSeccionRepository.registrarCursoNuevo(codigoCurso, nombreMateria);
            return "redirect:/admin/academico?correo=" + correo + "&exito=" + codificarMsj("Materia registrada exitosamente.");
        } catch (Exception e) {
            return "redirect:/admin/academico?correo=" + correo + "&error=" + codificarMsj("Error al registrar la materia.");
        }
    }

    // 2. Inscribir Estudiante
    @PostMapping("/inscribir")
    public String inscribirEstudiante(@RequestParam String correo, @RequestParam String cedulaEstudiante, @RequestParam String codigoCurso) {
        try {
            cursoSeccionRepository.inscribirEstudiante(cedulaEstudiante, codigoCurso);
            return "redirect:/admin/academico?correo=" + correo + "&exito=" + codificarMsj("Estudiante inscrito correctamente.");
        } catch (Exception e) {
            return "redirect:/admin/academico?correo=" + correo + "&error=" + codificarMsj("Fallo en inscripción. Verifique que la cédula sea de un estudiante activo o que no esté inscrito ya en esta materia.");
        }
    }

    // 3. Asignar Profesor
    @PostMapping("/asignar")
    public String asignarProfesor(@RequestParam String correo, @RequestParam String cedulaProfesor, @RequestParam String codigoCurso) {
        try {
            cursoSeccionRepository.asignarProfesor(cedulaProfesor, codigoCurso);
            return "redirect:/admin/academico?correo=" + correo + "&exito=" + codificarMsj("Carga docente asignada correctamente.");
        } catch (Exception e) {
            return "redirect:/admin/academico?correo=" + correo + "&error=" + codificarMsj("Fallo en asignación. Verifique que la cédula sea de un docente o que no tenga ya asignada esta materia.");
        }
    }

    // Función auxiliar para URL seguras
    private String codificarMsj(String mensaje) {
        return URLEncoder.encode(mensaje, StandardCharsets.UTF_8);
    }
}