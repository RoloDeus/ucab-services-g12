package com.ucab.services.services;

import com.ucab.services.repository.PagoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PagoService {

    private final PagoRepository repositorio;

    public PagoService(PagoRepository repositorio) {
        this.repositorio = repositorio;
    }

    // Admin
    public List<Map<String, Object>> listarFacturasPendientes() {
        return repositorio.listarFacturasPendientes();
    }

    // Estudiante / Profesor
    public List<Map<String, Object>> listarFacturasPendientesPorCorreo(String correo) {
        return repositorio.listarFacturasPendientesPorCorreo(correo);
    }

    public void registrarPago(String metodo, String numeroControlFactura, Long idUsuario, Double monto,
                               String correoOrigen, String nombreTitular, String codigoConfirmacion,
                               String numeroTarjeta, String fechaVencimiento,
                               String tipoRed, String companiaEmisora) {

        String idPago = ("zelle".equals(metodo) ? "PZ-" : "PT-") + System.currentTimeMillis();

        if ("zelle".equals(metodo)) {
            repositorio.registrarPagoZelle(idPago, numeroControlFactura, idUsuario, monto,
                    correoOrigen, nombreTitular, codigoConfirmacion);
        } else {
            repositorio.registrarPagoTarjeta(idPago, numeroControlFactura, idUsuario, monto,
                    numeroTarjeta, fechaVencimiento, tipoRed, companiaEmisora);
        }
    }
}