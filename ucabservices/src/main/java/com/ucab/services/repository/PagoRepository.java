package com.ucab.services.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class PagoRepository {

    private final JdbcTemplate jdbcTemplate;

    public PagoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Admin: ve todas las facturas pendientes
    public List<Map<String, Object>> listarFacturasPendientes() {
        return jdbcTemplate.queryForList(
            "SELECT f.numero_control, f.id_usuario, " +
            "       u.nombres || ' ' || u.apellidos AS titular, f.saldo_factura " +
            "FROM factura f JOIN usuario u ON u.id_usuario = f.id_usuario " +
            "WHERE f.saldo_factura > 0 ORDER BY f.numero_control");
    }

    // Estudiante / Profesor: solo sus propias facturas
    public List<Map<String, Object>> listarFacturasPendientesPorCorreo(String correo) {
        return jdbcTemplate.queryForList(
            "SELECT f.numero_control, f.id_usuario, " +
            "       u.nombres || ' ' || u.apellidos AS titular, f.saldo_factura " +
            "FROM factura f JOIN usuario u ON u.id_usuario = f.id_usuario " +
            "WHERE f.saldo_factura > 0 AND u.correo_institucional = ? ORDER BY f.numero_control",
            correo);
    }

    public void registrarPagoZelle(String idPago, String numeroControlFactura, Long idUsuario, Double monto,
                                    String correoOrigen, String nombreTitular, String codigoConfirmacion) {
        jdbcTemplate.update(
            "INSERT INTO pago_zelle " +
            "(id_pago, numero_control_factura, id_usuario, fecha_hora_pago, monto_operacion, " +
            " canal_operacion, correo_origen, nombre_titular_emisor, codigo_confirmacion) " +
            "VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, 'Portal Digital', ?, ?, ?)",
            idPago, numeroControlFactura, idUsuario, monto, correoOrigen, nombreTitular, codigoConfirmacion);
    }

    public void registrarPagoTarjeta(String idPago, String numeroControlFactura, Long idUsuario, Double monto,
                                      String numeroTarjeta, String fechaVencimiento,
                                      String tipoRed, String companiaEmisora) {
        jdbcTemplate.update(
            "INSERT INTO pago_tarjeta " +
            "(id_pago, numero_control_factura, id_usuario, fecha_hora_pago, monto_operacion, " +
            " canal_operacion, numero_tarjeta, fecha_vencimiento, tipo_red, compania_emisora) " +
            "VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, 'Taquilla Presencial', ?, ?, ?, ?)",
            idPago, numeroControlFactura, idUsuario, monto, numeroTarjeta, fechaVencimiento,
            tipoRed, companiaEmisora);
    }
}