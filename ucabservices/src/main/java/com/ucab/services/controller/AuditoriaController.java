package com.ucab.services.controller;

import com.ucab.services.entities.HistorialSesion;
import com.ucab.services.entities.Usuario;
import com.ucab.services.repository.HistorialSesionRepository;
import com.ucab.services.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/admin/auditoria")
public class AuditoriaController {

    private final UsuarioRepository usuarioRepository;
    private final HistorialSesionRepository historialSesionRepository;

    public AuditoriaController(UsuarioRepository usuarioRepository, HistorialSesionRepository historialSesionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.historialSesionRepository = historialSesionRepository;
    }

    @GetMapping("/sesiones")
    public String verHistorialSesiones(
            @RequestParam String correo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaHoraAcceso") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        // Validamos que el administrador exista
        Optional<Usuario> oAdmin = usuarioRepository.findByCorreoInstitucional(correo);
        if (oAdmin.isEmpty()) return "redirect:/login";

        model.addAttribute("admin", oAdmin.get());
        model.addAttribute("correo", correo);

        // Ordenamiento dinámico (Por defecto, las más recientes primero)
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                    Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<HistorialSesion> paginaSesiones = historialSesionRepository.findAll(pageable);

        // Pasamos los datos a la vista HTML
        model.addAttribute("pagina", paginaSesiones);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "panel-auditoria";
    }
}