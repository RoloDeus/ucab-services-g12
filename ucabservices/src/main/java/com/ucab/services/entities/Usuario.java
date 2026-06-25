package com.ucab.services.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "cedula", nullable = false, unique = true, length = 20)
    private String cedula;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.CHAR)
    @jakarta.persistence.Column(name = "genero", columnDefinition = "bpchar")
    private String genero;

    @Column(name = "calle", nullable = false, length = 150)
    private String calle;

    @Column(name = "zona", nullable = false, length = 150)
    private String zona;

    @Column(name = "ciudad", nullable = false, length = 150)
    private String ciudad;

    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @Column(name = "correo_institucional", nullable = false, unique = true, length = 100)
    private String correoInstitucional;

    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena;

    @Column(name = "estado_cuenta", nullable = false, length = 20)
    private String estadoCuenta = "Activa";

    @Column(name = "estatus_verificacion_dospasos")
    private Boolean estatusVerificacionDosPasos;

    @Column(name = "conteo_intentos_fallidos", nullable = false)
    private Integer conteoIntentosFallidos = 0;

    @Column(name = "fecha_cambio_clave")
    private LocalDateTime fechaCambioClave;

    @Column(name = "ultima_conexion")
    private LocalDateTime ultimaConexion;

    @Column(name = "indice_recurrencia", nullable = false)
    private Integer indiceRecurrencia = 0;

    @Column(name = "categoria_fidelidad", nullable = false, length = 20)
    private String categoriaFidelidad = "Regular";

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getCorreoInstitucional() { return correoInstitucional; }
    public void setCorreoInstitucional(String correoInstitucional) { this.correoInstitucional = correoInstitucional; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getEstadoCuenta() { return estadoCuenta; }
    public void setEstadoCuenta(String estadoCuenta) { this.estadoCuenta = estadoCuenta; }

    public Integer getConteoIntentosFallidos() { return conteoIntentosFallidos; }
    public void setConteoIntentosFallidos(Integer conteoIntentosFallidos) { this.conteoIntentosFallidos = conteoIntentosFallidos; }

    public LocalDateTime getUltimaConexion() { return ultimaConexion; }
    public void setUltimaConexion(LocalDateTime ultimaConexion) { this.ultimaConexion = ultimaConexion; }

    public Integer getIndiceRecurrencia() { return indiceRecurrencia; }
    public void setIndiceRecurrencia(Integer indiceRecurrencia) { this.indiceRecurrencia = indiceRecurrencia; }

    public String getCategoriaFidelidad() { return categoriaFidelidad; }
    public void setCategoriaFidelidad(String categoriaFidelidad) { this.categoriaFidelidad = categoriaFidelidad; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getCalle() { return calle; }
    public void setCalle(String calle) { this.calle = calle; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Boolean getEstatusVerificacionDosPasos() { return estatusVerificacionDosPasos; }
    public void setEstatusVerificacionDosPasos(Boolean estatusVerificacionDosPasos) { this.estatusVerificacionDosPasos = estatusVerificacionDosPasos; }

    public LocalDateTime getFechaCambioClave() { return fechaCambioClave; }
    public void setFechaCambioClave(LocalDateTime fechaCambioClave) { this.fechaCambioClave = fechaCambioClave; }

}
