package com.ucab.services.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/")
    public String mostrarInicio(Model model) {
        model.addAttribute("mensaje", "¡Entorno configurado con éxito! Java, Spring Boot y PostgreSQL están conectados.");
        
        return "index"; 
    }
}