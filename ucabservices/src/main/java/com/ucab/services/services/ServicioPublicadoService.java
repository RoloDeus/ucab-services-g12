package com.ucab.services.services;
 
import com.ucab.services.repository.ServicioPublicadoRepository;
import org.springframework.stereotype.Service;
 
import java.util.List;
import java.util.Map;
 
@Service
public class ServicioPublicadoService {
 
    private final ServicioPublicadoRepository repositorio;
 
    public ServicioPublicadoService(ServicioPublicadoRepository repositorio) {
        this.repositorio = repositorio;
    }
 
    public List<Map<String, Object>> listarTodos() {
        return repositorio.listarTodos();
    }
 
    public List<Map<String, Object>> listarCategorias() {
        return repositorio.listarCategorias();
    }
 
    public List<Map<String, Object>> listarSedes() {
        return repositorio.listarSedes();
    }
 
    public List<Map<String, Object>> listarEntidades() {
        return repositorio.listarEntidades();
    }
 
    // Aquí es donde, más adelante, se agregarían validaciones o reglas de
    // negocio adicionales antes de guardar (además de las que ya aplica
    // el propio trigger de la base de datos).
    public void crear(String categoria, Long idEntidad, String sede, String descripcion,
                       Double precioBase, Double ajuste) {
        repositorio.crear(categoria, idEntidad, sede, descripcion, precioBase, ajuste);
    }
}
 