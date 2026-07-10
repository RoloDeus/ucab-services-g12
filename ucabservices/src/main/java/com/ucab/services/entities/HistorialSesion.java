package com.ucab.services.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_sesion")
@IdClass(HistorialSesionId.class)
public class HistorialSesion {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", nullable = false)
    private Usuario usuario;

    @Id
    @Column(name = "fecha_hora_acceso", nullable = false)
    private LocalDateTime fechaHoraAcceso;

    @Column(name = "direccion_ip", nullable = false, length = 45)
    private String direccionIp;

    @Column(name = "uuid_dispositivo", nullable = false, length = 255)
    private String uuidDispositivo;

    @Column(name = "geolocalizacion_aproximada", nullable = false, length = 255)
    private String geolocalizacionAproximada;

    // --- Getters y Setters ---
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDateTime getFechaHoraAcceso() { return fechaHoraAcceso; }
    public void setFechaHoraAcceso(LocalDateTime fechaHoraAcceso) { this.fechaHoraAcceso = fechaHoraAcceso; }

    public String getDireccionIp() { return direccionIp; }
    public void setDireccionIp(String direccionIp) { this.direccionIp = direccionIp; }

    public String getUuidDispositivo() { return uuidDispositivo; }
    public void setUuidDispositivo(String uuidDispositivo) { this.uuidDispositivo = uuidDispositivo; }

    public String getGeolocalizacionAproximada() { return geolocalizacionAproximada; }
    public void setGeolocalizacionAproximada(String geolocalizacionAproximada) { this.geolocalizacionAproximada = geolocalizacionAproximada; }
}