package com.ucab.services.controller;
 
import com.ucab.services.services.ReporteService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
@RequestMapping("/api/reportes")
public class ReporteController {
 
    private final ReporteService servicio;
 
    public ReporteController(ReporteService servicio) {
        this.servicio = servicio;
    }
 
    // Ejemplo: GET /api/reportes/rep08, devuelve el PDF de REP08
    @GetMapping("/{clave}")
    public ResponseEntity<byte[]> ver(@PathVariable String clave) {
        byte[] pdf = servicio.generarReporte(clave);
 
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
            ContentDisposition.inline().filename(clave + ".pdf").build()
        );
 
        return new ResponseEntity<>(pdf, headers, org.springframework.http.HttpStatus.OK);
    }
}
 
