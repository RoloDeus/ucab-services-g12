package com.ucab.services.controller;

import com.ucab.services.entities.Edificacion;
import com.ucab.services.entities.Sede;
import com.ucab.services.entities.Usuario;
import com.ucab.services.repository.EdificacionRepository;
import com.ucab.services.repository.SedeRepository;
import com.ucab.services.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/jerarquia")
public class JerarquiaController {

    private final UsuarioRepository usuarioRepository;
    private final SedeRepository sedeRepository;
    private final EdificacionRepository edificacionRepository;

    public JerarquiaController(UsuarioRepository usuarioRepository, SedeRepository sedeRepository, EdificacionRepository edificacionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.sedeRepository = sedeRepository;
        this.edificacionRepository = edificacionRepository;
    }

    @GetMapping
    public String verJerarquia(
            @RequestParam String correo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "nombreSede") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Optional<Usuario> oAdmin = usuarioRepository.findByCorreoInstitucional(correo);
        if (oAdmin.isEmpty()) return "redirect:/login";

        model.addAttribute("admin", oAdmin.get());
        model.addAttribute("correo", correo);

        // Lista completa de sedes para los selectores de los formularios
        List<Sede> listaSedes = sedeRepository.findAll();
        model.addAttribute("listaSedes", listaSedes);

        // Paginación y ordenamiento dinámico de los Edificios
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                    Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Edificacion> paginaEdificios = edificacionRepository.findAll(pageable);

        model.addAttribute("pagina", paginaEdificios);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "jerarquia-admin";
    }

    // Endpoint para registrar una nueva Sede
    @PostMapping("/sede/guardar")
    public String guardarSede(@RequestParam String correoAdmin, @RequestParam String nombreSede) {
        Sede nuevaSede = new Sede(nombreSede);
        try {
            sedeRepository.save(nuevaSede);
        } catch (Exception e) {
            return "redirect:/admin/jerarquia?correo=" + correoAdmin + "&error=Error al guardar la sede: " + e.getMessage();
        }
        return "redirect:/admin/jerarquia?correo=" + correoAdmin;
    }

    // Endpoint para registrar un nuevo Edificio
    @PostMapping("/edificio/guardar")
    public String guardarEdificio(
            @RequestParam String correoAdmin,
            @RequestParam String nombreSede,
            @RequestParam String nombreEdificio,
            @RequestParam String direccionInterna) {

        Edificacion nuevoEdificio = new Edificacion();
        nuevoEdificio.setNombreSede(nombreSede);
        nuevoEdificio.setNombreEdificio(nombreEdificio);
        nuevoEdificio.setDireccionInterna(direccionInterna);

        try {
            edificacionRepository.save(nuevoEdificio);
        } catch (Exception e) {
            return "redirect:/admin/jerarquia?correo=" + correoAdmin + "&error=Error al guardar el edificio: " + e.getMessage();
        }
        return "redirect:/admin/jerarquia?correo=" + correoAdmin;
    }

    @PostMapping("/sede/eliminar")
    public String eliminarSede(@RequestParam String correoAdmin, @RequestParam String nombreSede) {
        try {
            sedeRepository.deleteById(nombreSede);
            return "redirect:/admin/jerarquia?correo=" + correoAdmin + "&exito=" + URLEncoder.encode("Sede eliminada correctamente.", StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "redirect:/admin/jerarquia?correo=" + correoAdmin + "&error=" + URLEncoder.encode("No se puede eliminar la sede porque aún tiene edificios asignados.", StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/edificio/eliminar")
    public String eliminarEdificio(@RequestParam String correoAdmin, @RequestParam String nombreSede, @RequestParam String nombreEdificio) {
        try {
            Optional<Edificacion> edificio = edificacionRepository.findAll().stream()
                    .filter(e -> nombreSede.equals(e.getNombreSede()) && nombreEdificio.equals(e.getNombreEdificio()))
                    .findFirst();
            if (edificio.isPresent()) {
                edificacionRepository.delete(edificio.get());
            }
            return "redirect:/admin/jerarquia?correo=" + correoAdmin + "&exito=" + URLEncoder.encode("Edificio eliminado correctamente.", StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "redirect:/admin/jerarquia?correo=" + correoAdmin + "&error=" + URLEncoder.encode("No se puede eliminar el edificio porque contiene aulas registradas.", StandardCharsets.UTF_8);
        }
    }
}