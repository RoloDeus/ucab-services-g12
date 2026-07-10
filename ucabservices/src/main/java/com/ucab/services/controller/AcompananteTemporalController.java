package com.ucab.services.controller;

import com.ucab.services.entities.AcompananteTemporal;
import com.ucab.services.entities.Usuario;
import com.ucab.services.repository.AcompananteTemporalRepository;
import com.ucab.services.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/perfil/acompanantes")
public class AcompananteTemporalController {

    private final UsuarioRepository usuarioRepository;
    private final AcompananteTemporalRepository acompananteTemporalRepository;

    public AcompananteTemporalController(UsuarioRepository usuarioRepository, AcompananteTemporalRepository acompananteTemporalRepository) {
        this.usuarioRepository = usuarioRepository;
        this.acompananteTemporalRepository = acompananteTemporalRepository;
    }

    @GetMapping
    public String listarAcompanantes(
            @RequestParam String correo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "fechaExpiracion") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        Optional<Usuario> oUsuario = usuarioRepository.findByCorreoInstitucional(correo);
        if (oUsuario.isEmpty()) return "redirect:/login";

        Usuario usuario = oUsuario.get();
        model.addAttribute("usuario", usuario);
        model.addAttribute("correo", correo);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                    Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AcompananteTemporal> paginaAcompanantes = acompananteTemporalRepository.findByUsuario_IdUsuario(usuario.getIdUsuario(), pageable);

        model.addAttribute("pagina", paginaAcompanantes);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "acompanante-temporal";
    }

    @PostMapping("/guardar")
    public String guardarAcompanante(
            @RequestParam String correoTitular,
            @RequestParam Long ciAcompanante,
            @RequestParam String nombres,
            @RequestParam String apellidos) { // Ya no recibimos la fecha de la vista

        Optional<Usuario> oUsuario = usuarioRepository.findByCorreoInstitucional(correoTitular);
        if (oUsuario.isPresent()) {
            AcompananteTemporal acompanante = new AcompananteTemporal();
            acompanante.setCiAcompanante(ciAcompanante);
            acompanante.setUsuario(oUsuario.get());
            acompanante.setNombres(nombres);
            acompanante.setApellidos(apellidos);
            
            // REGLA DE NEGOCIO ESTRICTA: El pase expira indefectiblemente el día de hoy
            acompanante.setFechaExpiracion(LocalDate.now());

            try {
                acompananteTemporalRepository.save(acompanante);
            } catch (Exception e) {
                return "redirect:/perfil/acompanantes?correo=" + correoTitular + "&error=" + e.getMessage();
            }
        }
        return "redirect:/perfil/acompanantes?correo=" + correoTitular;
    }
}