package com.ucab.services.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaController {

    // Cuando el usuario entre a localhost:8080/login
    @GetMapping("/login")
    public String mostrarLogin() {
        // Thymeleaf buscará automáticamente "login.html" en la carpeta templates
        return "login"; 
    }

    // Cuando el usuario entre a localhost:8080/registro
    @GetMapping("/registro")
    public String mostrarRegistro() {
        // Thymeleaf buscará automáticamente "registro.html" en la carpeta templates
        return "registro"; 
    }
}
