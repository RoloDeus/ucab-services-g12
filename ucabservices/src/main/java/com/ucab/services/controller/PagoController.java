package com.ucab.services.controller;

import com.ucab.services.services.PagoService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService servicio;

    public PagoController(PagoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/facturas-pendientes")
    public ResponseEntity<?> facturasPendientes(
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String rol) {

        // Admin ve todo; cualquier otro rol solo ve sus propias facturas
        if (rol != null && rol.toLowerCase().contains("admin")) {
            return ResponseEntity.ok(servicio.listarFacturasPendientes());
        }

        if (correo == null || correo.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Se requiere el correo del usuario."));
        }

        return ResponseEntity.ok(servicio.listarFacturasPendientesPorCorreo(correo));
    }

    public record NuevoPago(String metodo, String numeroControlFactura, Long idUsuario, Double monto,
                             String correoOrigen, String nombreTitular, String codigoConfirmacion,
                             String numeroTarjeta, String fechaVencimiento,
                             String tipoRed, String companiaEmisora) {}

    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody NuevoPago datos) {
        try {
            servicio.registrarPago(datos.metodo(), datos.numeroControlFactura(), datos.idUsuario(),
                    datos.monto(), datos.correoOrigen(), datos.nombreTitular(), datos.codigoConfirmacion(),
                    datos.numeroTarjeta(), datos.fechaVencimiento(), datos.tipoRed(), datos.companiaEmisora());
            return ResponseEntity.ok(Map.of("mensaje", "Pago registrado. Saldo actualizado."));
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", mensajeLimpio(e)));
        }
    }

    private String mensajeLimpio(DataAccessException e) {
        Throwable causa = e.getRootCause();
        String texto = (causa != null ? causa.getMessage() : e.getMessage());
        if (texto == null) return "Ocurrió un error al registrar el pago.";
        texto = texto.replace("ERROR: ", "");
        int saltoLinea = texto.indexOf('\n');
        return saltoLinea > 0 ? texto.substring(0, saltoLinea) : texto;
    }
}