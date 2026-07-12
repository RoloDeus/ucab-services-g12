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
    public List<Map<String, Object>> facturasPendientes() {
        return servicio.listarFacturasPendientes();
    }
 
    // Trae todos los campos posibles de los dos métodos; el formulario solo
    // llena los que correspondan según cuál se haya elegido.
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
            // Aquí "aterriza" el error del trigger HU-62: pago que excede el saldo,
            // o intento de pagar una factura ya liquidada.
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
 
