package com.ucab.services.controller;

import com.ucab.services.entities.PeriodoVinculacion;
import com.ucab.services.entities.Usuario;
import com.ucab.services.repository.PeriodoVinculacionRepository;
import com.ucab.services.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class InicioController {

    private final UsuarioRepository usuarioRepository;
    private final PeriodoVinculacionRepository vinculacionRepository;

    public InicioController(UsuarioRepository usuarioRepository, PeriodoVinculacionRepository vinculacionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.vinculacionRepository = vinculacionRepository;
    }

    @GetMapping("/inicio")
    public String mostrarDashboard(@RequestParam String correo, Model model) {
        Optional<Usuario> oUsuario = usuarioRepository.findByCorreoInstitucional(correo);
        if (oUsuario.isEmpty()) return "redirect:/login";

        Usuario usuario = oUsuario.get();
        model.addAttribute("usuario", usuario);
        model.addAttribute("correo", correo);

        // Determinamos el rol activo
        List<PeriodoVinculacion> vinculaciones = vinculacionRepository.findByUsuario_IdUsuario(usuario.getIdUsuario());
        String rolActivo = "Invitado";
        
        if (!vinculaciones.isEmpty()) {
            rolActivo = vinculaciones.get(vinculaciones.size() - 1).getRolInstitucional();
        }
        
        model.addAttribute("rolActivo", rolActivo);

        // Banderas para encender módulos en la vista Thymeleaf según el documento del proyecto
        model.addAttribute("esEstudiante", rolActivo.equalsIgnoreCase("Estudiante"));
        model.addAttribute("esProfesor", rolActivo.equalsIgnoreCase("Profesor"));
        model.addAttribute("esAdmin", rolActivo.equalsIgnoreCase("Personal Administrativo"));
        model.addAttribute("esEgresado", rolActivo.equalsIgnoreCase("Egresado"));

        return "inicio"; 
    }
}