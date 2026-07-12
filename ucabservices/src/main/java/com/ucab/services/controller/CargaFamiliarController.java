package com.ucab.services.controller;

import com.ucab.services.entities.BeneficiarioFamiliar;
import com.ucab.services.entities.Usuario;
import com.ucab.services.repository.BeneficiarioFamiliarRepository;
import com.ucab.services.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/perfil/carga-familiar")
public class CargaFamiliarController {

    private final UsuarioRepository usuarioRepository;
    private final BeneficiarioFamiliarRepository beneficiarioFamiliarRepository;

    public CargaFamiliarController(UsuarioRepository usuarioRepository, BeneficiarioFamiliarRepository beneficiarioFamiliarRepository) {
        this.usuarioRepository = usuarioRepository;
        this.beneficiarioFamiliarRepository = beneficiarioFamiliarRepository;
    }

    // Listar la carga familiar con paginación y ordenamiento
    @GetMapping
    public String listarFamiliares(
            @RequestParam String correo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "apellidos") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Optional<Usuario> oUsuario = usuarioRepository.findByCorreoInstitucional(correo);
        if (oUsuario.isEmpty()) return "redirect:/login";

        Usuario usuario = oUsuario.get();
        model.addAttribute("usuario", usuario);
        model.addAttribute("correo", correo);

        // Configuración dinámica del ordenamiento
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                    Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BeneficiarioFamiliar> paginaFamiliares = beneficiarioFamiliarRepository.findByUsuario_IdUsuario(usuario.getIdUsuario(), pageable);

        // Inyección de atributos a Thymeleaf
        model.addAttribute("pagina", paginaFamiliares);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "carga-familiar";
    }

    // Guardar nuevo familiar respetando el esquema relacional
    @PostMapping("/guardar")
    @Transactional // IMPORTANTE: Agrega esta etiqueta
    public String guardarFamiliar(
            @RequestParam String correoTitular,
            @RequestParam Long ciFamiliar,
            @RequestParam String nombres,
            @RequestParam String apellidos,
            @RequestParam String parentesco,
            @RequestParam String fechaNacimiento,
            @RequestParam String esquemaVacunacion,
            @RequestParam String centroEducacionInicial,
            @RequestParam String constanciaEstudios,
            @RequestParam String certificadoSolteria) {

        Optional<Usuario> oUsuario = usuarioRepository.findByCorreoInstitucional(correoTitular);
        if (oUsuario.isPresent()) {
            BeneficiarioFamiliar familiar = new BeneficiarioFamiliar();
            familiar.setCiFamiliar(ciFamiliar);
            familiar.setUsuario(oUsuario.get());
            familiar.setNombres(nombres);
            familiar.setApellidos(apellidos);
            familiar.setParentesco(parentesco);
            familiar.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
            familiar.setEsquemaVacunacion(esquemaVacunacion);
            familiar.setCentroEducacionInicial(centroEducacionInicial);
            familiar.setConstanciaEstudiosUniversitarios(constanciaEstudios);
            familiar.setCertificadoSolteria(certificadoSolteria);

            try {
                // 1. Guardamos el nuevo familiar en la base de datos
                beneficiarioFamiliarRepository.save(familiar);
                
                // 2. NUEVO: Ejecutamos el Procedimiento Almacenado para que valide y actualice
                beneficiarioFamiliarRepository.ejecutarProcedimientoMayoriaEdad();
                
            } catch (Exception e) {
                return "redirect:/perfil/carga-familiar?correo=" + correoTitular + "&error=" + e.getMessage();
            }
        }
        return "redirect:/perfil/carga-familiar?correo=" + correoTitular;
    }
}