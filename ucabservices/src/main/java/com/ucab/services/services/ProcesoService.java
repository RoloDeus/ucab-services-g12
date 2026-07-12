package com.ucab.services.services;
 
import com.ucab.services.repository.ProcesoRepository;
import org.springframework.stereotype.Service;
 
@Service
public class ProcesoService {
 
    private final ProcesoRepository repositorio;
 
    public ProcesoService(ProcesoRepository repositorio) {
        this.repositorio = repositorio;
    }
 
    // La clave que llega del botón se traduce al nombre real del procedimiento.
    public String ejecutarProceso(String clave) {
        return switch (clave) {
            case "mayoria-edad"   -> repositorio.ejecutar("sp_detectar_mayoria_edad");
            case "indices"        -> repositorio.ejecutar("sp_actualizar_indices_recurrencia");
            case "fidelidad"      -> repositorio.ejecutar("sp_actualizar_categorias_fidelidad");
            case "cierre-mensual" -> repositorio.ejecutar("sp_cierre_mensual_facturas");
            case "limpieza"       -> repositorio.ejecutar("sp_limpieza_soft_delete");
            default -> throw new IllegalArgumentException("Proceso no reconocido: " + clave);
        };
    }
 
    public String sincronizarTasaBcv(Double tasa) {
        return repositorio.sincronizarTasaBcv(tasa);
    }
}
 
