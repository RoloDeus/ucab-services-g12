package com.ucab.services.controller;

import com.ucab.services.entities.EspacioFisico;
import com.ucab.services.entities.EspacioFisicoId;
import com.ucab.services.entities.Usuario;
import com.ucab.services.repository.EspacioFisicoRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import com.ucab.services.entities.Sede;
import com.ucab.services.repository.SedeRepository;
import java.util.List;

@Controller
@RequestMapping("/admin/infraestructura")
public class InfraestructuraController {

    private final UsuarioRepository usuarioRepository;
    private final EspacioFisicoRepository espacioFisicoRepository;
    private final SedeRepository sedeRepository;

    public InfraestructuraController(UsuarioRepository usuarioRepository,
                                     EspacioFisicoRepository espacioFisicoRepository,
                                     SedeRepository sedeRepository) {
        this.usuarioRepository = usuarioRepository;
        this.espacioFisicoRepository = espacioFisicoRepository;
        this.sedeRepository = sedeRepository;
    }

    // Listar espacios físicos con paginación y ordenamiento
    @GetMapping
    public String listarEspacios(
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

        // Lógica de ordenamiento dinámico
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                    Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<EspacioFisico> paginaEspacios = espacioFisicoRepository.findAll(pageable);

        // Envío de variables a la vista
        model.addAttribute("pagina", paginaEspacios);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        
        List<Sede> listaSedes = sedeRepository.findAll();
        model.addAttribute("listaSedes", listaSedes);
        return "infraestructura-admin";
    }

    // Guardar un nuevo espacio físico
    @PostMapping("/guardar")
    @Transactional // IMPORTANTE: Garantiza que si algo falla, no se guarda nada a medias
    public String guardarEspacio(
            @RequestParam String correoAdmin,
            @RequestParam String nombreSede,
            @RequestParam String nombreEdificio,
            @RequestParam String idEspacio,
            @RequestParam String direccionInterna,
            @RequestParam Integer capacidadMaximaAforo,
            @RequestParam String tipoMobiliario,
            @RequestParam String estadoMantenimiento,
            @RequestParam String registroDisponibilidad) {

        EspacioFisico espacio = new EspacioFisico();
        espacio.setNombreSede(nombreSede);
        espacio.setNombreEdificio(nombreEdificio);
        espacio.setIdEspacio(idEspacio);
        espacio.setDireccionInterna(direccionInterna);
        espacio.setCapacidadMaximaAforo(capacidadMaximaAforo);
        espacio.setTipoMobiliario(tipoMobiliario);
        espacio.setEstadoMantenimiento(estadoMantenimiento);
        espacio.setRegistroDisponibilidad(registroDisponibilidad);

        try {
            // 1. Magia Pura: Aseguramos que la Sede exista
            espacioFisicoRepository.asegurarSedeExistente(nombreSede);
            
            // 2. Magia Pura: Aseguramos que el Edificio exista
            espacioFisicoRepository.asegurarEdificacionExistente(nombreSede, nombreEdificio, direccionInterna);
            
            // 3. Ahora sí, guardamos el salón sin que la Llave Foránea explote
            espacioFisicoRepository.save(espacio);
            
        } catch (Exception e) {
            String mensajeError = "Error de Integridad: " + e.getMessage();
            return "redirect:/admin/infraestructura?correo=" + correoAdmin + "&error=" + URLEncoder.encode(mensajeError, StandardCharsets.UTF_8);
        }
        
        return "redirect:/admin/infraestructura?correo=" + correoAdmin;
    }

    @PostMapping("/eliminar")
    public String eliminarEspacio(
            @RequestParam String correoAdmin, 
            @RequestParam String idEspacio,
            @RequestParam String nombreSede,
            @RequestParam String nombreEdificio) {
        
        try {
            // 1. Instanciamos la llave primaria compuesta
            EspacioFisicoId espacioFisicoId = new EspacioFisicoId();
            
            // 2. Llenamos TODOS los campos que conforman esa llave
            espacioFisicoId.setIdEspacio(idEspacio);
            espacioFisicoId.setNombreSede(nombreSede);
            espacioFisicoId.setNombreEdificio(nombreEdificio);
            
            // 3. Ejecutamos el DELETE mágico
            espacioFisicoRepository.deleteById(espacioFisicoId);
            
            String msjExito = "Espacio físico eliminado correctamente.";
            return "redirect:/admin/infraestructura?correo=" + correoAdmin + "&exito=" + URLEncoder.encode(msjExito, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            String msjError = "No se puede eliminar el espacio. Es posible que esté siendo utilizado en la carga académica actual.";
            return "redirect:/admin/infraestructura?correo=" + correoAdmin + "&error=" + URLEncoder.encode(msjError, StandardCharsets.UTF_8);
        }
    }

    // Endpoint para actualizar un espacio físico
    @PostMapping("/actualizar")
    @Transactional
    public String actualizarEspacio(
            @RequestParam String correoAdmin,
            @RequestParam String idEspacio,
            @RequestParam String nombreSede, 
            @RequestParam String nombreEdificio, 
            @RequestParam Integer nuevaCapacidad,
            @RequestParam String nuevoEstado,
            @RequestParam String nuevaDisponibilidad) {

        try {
            EspacioFisicoId id = new EspacioFisicoId();
            id.setIdEspacio(idEspacio);
            id.setNombreSede(nombreSede);
            id.setNombreEdificio(nombreEdificio);

            Optional<EspacioFisico> oEspacio = espacioFisicoRepository.findById(id);
            
            if (oEspacio.isPresent()) {
                EspacioFisico espacio = oEspacio.get();
                espacio.setCapacidadMaximaAforo(nuevaCapacidad);
                espacio.setEstadoMantenimiento(nuevoEstado);
                espacio.setRegistroDisponibilidad(nuevaDisponibilidad);
                
                espacioFisicoRepository.save(espacio);
                return "redirect:/admin/infraestructura?correo=" + correoAdmin + "&exito=" + URLEncoder.encode("Espacio actualizado correctamente.", StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            return "redirect:/admin/infraestructura?correo=" + correoAdmin + "&error=" + URLEncoder.encode("Error al actualizar.", StandardCharsets.UTF_8);
        }
        return "redirect:/admin/infraestructura?correo=" + correoAdmin;
    }
}