package com.ucab.services.controller;

import com.ucab.services.entities.Estudiante;
import com.ucab.services.entities.PeriodoVinculacion;
import com.ucab.services.entities.Preparaduria;
import com.ucab.services.entities.Profesor;
import com.ucab.services.repository.PreparaduriaRepository;
import com.ucab.services.entities.Usuario;
import com.ucab.services.repository.EstudianteRepository;
import com.ucab.services.repository.UsuarioRepository;
import com.ucab.services.repository.PeriodoVinculacionRepository;
import com.ucab.services.repository.ProfesorRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.ucab.services.entities.Beca;
import com.ucab.services.repository.BecaRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.transaction.annotation.Transactional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.ucab.services.entities.CursoSeccion;
import com.ucab.services.repository.CursoSeccionRepository;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final PreparaduriaRepository preparaduriaRepository;
    private final BecaRepository becaRepository;
    private final PeriodoVinculacionRepository periodoVinculacionRepository;
    private final ProfesorRepository profesorRepository;
    private final CursoSeccionRepository cursoSeccionRepository;

    public PerfilController(UsuarioRepository usuarioRepository, EstudianteRepository estudianteRepository, PreparaduriaRepository preparaduriaRepository, BecaRepository becaRepository, PeriodoVinculacionRepository periodoVinculacionRepository, ProfesorRepository profesorRepository, CursoSeccionRepository cursoSeccionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.estudianteRepository = estudianteRepository;
        this.preparaduriaRepository = preparaduriaRepository;
        this.becaRepository = becaRepository;
        this.periodoVinculacionRepository = periodoVinculacionRepository;
        this.profesorRepository = profesorRepository;
        this.cursoSeccionRepository = cursoSeccionRepository;
    }

    // Endpoint: localhost:8080/perfil/academico?correo=tu_correo
    @GetMapping("/academico")
    public String mostrarFichaAcademica(@RequestParam String correo, Model model) {
        Optional<Usuario> oUsuario = usuarioRepository.findByCorreoInstitucional(correo);
        if (!oUsuario.isPresent()) {
            return "redirect:/login"; 
        }

        Usuario usuario = oUsuario.get();
        model.addAttribute("usuario", usuario); 

        // Buscamos el historial de vinculaciones como estudiante
        List<Estudiante> vinculacionesEstudiante = estudianteRepository.findByUsuario_IdUsuario(usuario.getIdUsuario());

        // Si tiene vinculaciones como estudiante, buscamos sus atributos derivados
        if (!vinculacionesEstudiante.isEmpty()) {
            Estudiante estudianteActivo = vinculacionesEstudiante.get(vinculacionesEstudiante.size() - 1);
            model.addAttribute("estudiante", estudianteActivo);

            // Buscamos si tiene preparadurías activas (HU-14)
            List<Preparaduria> preparadurias = preparaduriaRepository.findByEstudiante_Usuario_IdUsuario(usuario.getIdUsuario());
            if (!preparadurias.isEmpty()) {
                model.addAttribute("preparaduria", preparadurias.get(0));
            }
            
            // NUEVO: Buscamos si tiene beca activa (HU-13) ADENTRO del if
            List<Beca> becas = becaRepository.findByEstudiante_Usuario_IdUsuario(usuario.getIdUsuario());
            if (!becas.isEmpty()) {
                model.addAttribute("beca", becas.get(0));
            }

            // NUEVO: Buscamos la carga académica (Materias inscritas)
            List<CursoSeccion> materiasInscritas = cursoSeccionRepository.findCursosInscritosPorEstudiante(usuario.getIdUsuario());
            model.addAttribute("materiasInscritas", materiasInscritas);
        }

        return "ficha-academica"; 
    }

    // Endpoint: localhost:8080/perfil/mis-preparadurias
    @GetMapping("/mis-preparadurias")
    public String mostrarMisPreparadurias(
            @RequestParam String correo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "asignaturaAsignada") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        Optional<Usuario> oUsuario = usuarioRepository.findByCorreoInstitucional(correo);
        if (oUsuario.isEmpty()) return "redirect:/login";

        Usuario usuario = oUsuario.get();
        model.addAttribute("usuario", usuario);

        // Configuramos la dirección del ordenamiento (Ascendente o Descendente)
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                    Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        
        // Creamos el objeto Pageable con la página actual, el tamaño de la tabla y el orden
        Pageable pageable = PageRequest.of(page, size, sort);

        // Hacemos la consulta paginada a la base de datos
        Page<Preparaduria> paginaPreparadurias = preparaduriaRepository.findByEstudiante_Usuario_IdUsuario(usuario.getIdUsuario(), pageable);

        // Pasamos todos los datos a la vista Thymeleaf
        model.addAttribute("pagina", paginaPreparadurias);
        model.addAttribute("currentPage", page);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("correo", correo);

        return "mis-preparadurias";
    }

    // HU-11: ENRUTADOR DINÁMICO
    @GetMapping("/router")
    public String enrutadorDinamico(@RequestParam String correo) {
        Optional<Usuario> oUsuario = usuarioRepository.findByCorreoInstitucional(correo);
        if (oUsuario.isEmpty()) return "redirect:/login";

        // Buscamos todas las vinculaciones del usuario
        List<PeriodoVinculacion> vinculaciones = periodoVinculacionRepository.findByUsuario_IdUsuario(oUsuario.get().getIdUsuario());
        
        if (vinculaciones.isEmpty()) return "redirect:/login";

        // Tomamos el último rol activo
        PeriodoVinculacion rolActivo = vinculaciones.get(vinculaciones.size() - 1);

        // Lógica de Redirección Dinámica por Rol
        if (rolActivo.getRolInstitucional().equalsIgnoreCase("Estudiante")) {
            return "redirect:/perfil/academico?correo=" + correo;
        } else if (rolActivo.getRolInstitucional().equalsIgnoreCase("Profesor")) {
            return "redirect:/perfil/docente?correo=" + correo;
        }

        return "redirect:/login";
    }

    // HU-13: SOLICITUD DE BECA DINÁMICA
    @PostMapping("/beca/solicitar")
    public String solicitarBeca(
            @RequestParam String correo,
            @RequestParam String cedula,
            @RequestParam String tipoBeca) {
        
        try {
            // Llamamos a la base de datos. Si el promedio no da, PostgreSQL lanzará un error aquí.
            usuarioRepository.solicitarBeca(cedula, tipoBeca);
            
            // Si pasa esta línea, significa que la BD aprobó la beca
            String msjExito = "¡Felicidades! El sistema ha evaluado tu promedio y la beca ha sido aprobada y asignada.";
            return "redirect:/perfil/academico?correo=" + correo + "&exito=" + URLEncoder.encode(msjExito, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            // Si PostgreSQL rechaza la solicitud (RAISE EXCEPTION), caemos aquí
            String msjError = "Solicitud rechazada por el sistema. No cumples con el índice académico exigido o ya posees una beca activa.";
            return "redirect:/perfil/academico?correo=" + correo + "&error=" + URLEncoder.encode(msjError, StandardCharsets.UTF_8);
        }
    }

    // HU-15: EXPEDIENTE DOCENTE
    @GetMapping("/docente")
    public String mostrarExpedienteDocente(@RequestParam String correo, Model model) {
        Optional<Usuario> oUsuario = usuarioRepository.findByCorreoInstitucional(correo);
        if (oUsuario.isEmpty()) return "redirect:/login";

        Usuario usuario = oUsuario.get();
        model.addAttribute("usuario", usuario);

        // Traemos todo el historial para la línea de tiempo (Trayectoria Institucional)
        List<PeriodoVinculacion> historial = periodoVinculacionRepository.findByUsuario_IdUsuario(usuario.getIdUsuario());
        model.addAttribute("historial", historial);

        // Traemos la ficha específica de Profesor
        List<Profesor> vinculacionesProfesor = profesorRepository.findByUsuario_IdUsuario(usuario.getIdUsuario());
        if (!vinculacionesProfesor.isEmpty()) {
            model.addAttribute("profesor", vinculacionesProfesor.get(vinculacionesProfesor.size() - 1));
        }

        // NUEVO: Buscamos las materias que imparte este docente
        List<CursoSeccion> materiasImpartidas = cursoSeccionRepository.findCursosImpartidosPorProfesor(usuario.getIdUsuario());
        model.addAttribute("materiasImpartidas", materiasImpartidas);

        return "expediente-docente";
    }
 
}