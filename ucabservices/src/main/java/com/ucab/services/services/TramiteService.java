package com.ucab.services.services;
 
import com.ucab.services.repository.TramiteRepository;
import org.springframework.stereotype.Service;
 
import java.util.List;
import java.util.Map;
 
@Service
public class TramiteService {
 
    private final TramiteRepository repositorio;
 
    public TramiteService(TramiteRepository repositorio) {
        this.repositorio = repositorio;
    }
 
    public List<Map<String, Object>> listarPasos() {
        return repositorio.listarPasos();
    }
 
    public void completarPaso(Long idSolicitud, Long idUsuario, Integer secuenciaPaso) {
        repositorio.completarPaso(idSolicitud, idUsuario, secuenciaPaso);
    }
}
