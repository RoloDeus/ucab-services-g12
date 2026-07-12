package com.ucab.services.services;
 
import com.ucab.services.repository.ReporteRepository;
import org.springframework.stereotype.Service;
 
import java.util.List;
import java.util.Map;
 
@Service
public class ReporteService {
 
    private final ReporteRepository repositorio;
    private final JsreportClient jsreportClient;
 
    public ReporteService(ReporteRepository repositorio, JsreportClient jsreportClient) {
        this.repositorio = repositorio;
        this.jsreportClient = jsreportClient;
    }
 
    // Un solo método sirve para los 8 reportes
    // busca los datos correctos y le pide a jsreport 
    // que arme el PDF con la plantilla del mismo nombre.
    public byte[] generarReporte(String clave) {
        List<Map<String, Object>> filas = switch (clave) {
            case "rep01" -> repositorio.obtenerRep01();
            case "rep02" -> repositorio.obtenerRep02();
            case "rep03" -> repositorio.obtenerRep03();
            case "rep04" -> repositorio.obtenerRep04();
            case "rep05" -> repositorio.obtenerRep05();
            case "rep06" -> repositorio.obtenerRep06();
            case "rep07" -> repositorio.obtenerRep07();
            case "rep08" -> repositorio.obtenerRep08();
            default -> throw new IllegalArgumentException("Reporte no reconocido: " + clave);
        };
        return jsreportClient.generarPdf(clave, filas);
    }
}
 