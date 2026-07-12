package com.ucab.services.controller;
 
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
 
// OJO: @Controller (no @RestController), porque este NO devuelve JSON,
// devuelve el NOMBRE de una plantilla para que Thymeleaf la renderice.
@Controller
public class PaginasController {
 
    // Cuando alguien visite /servicios, Spring busca
    // src/main/resources/templates/servicios.html y la muestra.
    @GetMapping("/servicios")
    public String servicios() {
        return "servicios";
    }

    @GetMapping("/pagos")
    public String pagos() {
        return "pagos";
    }

    @GetMapping("/tramites")
    public String tramites() {
        return "tramites";
    }

    @GetMapping("/reportes")
    public String reportes() {
        return "reportes";
    }

    @GetMapping("/procesos")
    public String procesos() {
        return "procesos";
    }
}
 
