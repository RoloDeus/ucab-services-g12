package com.ucab.services.repository;

import com.ucab.services.entities.CursoSeccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CursoSeccionRepository extends JpaRepository<CursoSeccion, String> {

    // Busca los cursos que un Estudiante tiene inscritos
    @Query(value = "SELECT c.* FROM curso_seccion c INNER JOIN inscribe i ON c.codigo_curso = i.codigo_curso WHERE i.id_estudiante = :idUsuario", nativeQuery = true)
    List<CursoSeccion> findCursosInscritosPorEstudiante(@Param("idUsuario") Long idUsuario);

    // Busca los cursos que un Profesor imparte
    @Query(value = "SELECT c.* FROM curso_seccion c INNER JOIN imparte imp ON c.codigo_curso = imp.codigo_curso WHERE imp.id_profesor = :idUsuario", nativeQuery = true)
    List<CursoSeccion> findCursosImpartidosPorProfesor(@Param("idUsuario") Long idUsuario);

    // 1. Registrar una nueva materia en el catálogo
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO curso_seccion (codigo_curso, nombre_materia) VALUES (:codigo, :nombre) ON CONFLICT DO NOTHING", nativeQuery = true)
    void registrarCursoNuevo(@Param("codigo") String codigo, @Param("nombre") String nombre);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO inscribe (id_estudiante, fecha_inicio_estudiante, codigo_curso) " +
                   "SELECT e.id_usuario, e.fecha_inicio, :codigoCurso " +
                   "FROM estudiante e JOIN usuario u ON e.id_usuario = u.id_usuario " +
                   "WHERE u.cedula = :cedula " +
                   "ORDER BY e.fecha_inicio DESC LIMIT 1", nativeQuery = true)
    void inscribirEstudiante(@Param("cedula") String cedula, @Param("codigoCurso") String codigoCurso);

    // 3. Asignar Profesor (Solo en su periodo docente más reciente)
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO imparte (id_profesor, fecha_inicio_profesor, codigo_curso) " +
                   "SELECT p.id_usuario, p.fecha_inicio, :codigoCurso " +
                   "FROM profesor p JOIN usuario u ON p.id_usuario = u.id_usuario " +
                   "WHERE u.cedula = :cedula " +
                   "ORDER BY p.fecha_inicio DESC LIMIT 1", nativeQuery = true)
    void asignarProfesor(@Param("cedula") String cedula, @Param("codigoCurso") String codigoCurso);
}