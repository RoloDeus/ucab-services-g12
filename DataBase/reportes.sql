
-- ============================================================
-- REP-01 / HU-55 — Auditoría de Cuellos de Botella
-- Detecta qué oficina/paso tarda más en los trámites.
-- ============================================================
DROP VIEW IF EXISTS vista_rep01_cuellos_por_oficina;
DROP VIEW IF EXISTS vista_rep01_duracion_pasos;

CREATE VIEW vista_rep01_duracion_pasos AS
SELECT
    pa.id_solicitud,
    pa.id_usuario,
    pa.secuencia_paso,
    pa.oficina_responsable,
    pa.estado_paso,
    pa.fecha_hora_finalizacion_exacta,
    LAG(pa.fecha_hora_finalizacion_exacta)
        OVER (PARTITION BY pa.id_solicitud, pa.id_usuario
              ORDER BY pa.secuencia_paso) AS fin_paso_anterior,
    EXTRACT(EPOCH FROM (
        pa.fecha_hora_finalizacion_exacta
        - LAG(pa.fecha_hora_finalizacion_exacta)
            OVER (PARTITION BY pa.id_solicitud, pa.id_usuario
                  ORDER BY pa.secuencia_paso)
    )) / 86400.0 AS dias_duracion
FROM paso_actividad pa;

CREATE VIEW vista_rep01_cuellos_por_oficina AS
SELECT
    oficina_responsable,
    COUNT(*)                     AS pasos_medidos,
    ROUND(AVG(dias_duracion), 2) AS dias_promedio,
    ROUND(MAX(dias_duracion), 2) AS dias_maximo
FROM vista_rep01_duracion_pasos
WHERE dias_duracion IS NOT NULL
GROUP BY oficina_responsable
ORDER BY dias_promedio DESC;


-- ============================================================
-- REP-02 / HU-56 — Conciliación Financiera
-- Cruza facturas vs. pagos. 
-- ============================================================
DROP VIEW IF EXISTS vista_rep02_conciliacion;

CREATE VIEW vista_rep02_conciliacion AS
SELECT
    f.numero_control,
    f.id_usuario,
    u.nombres || ' ' || u.apellidos AS usuario,
    f.fecha_emision,
    (f.saldo_factura + COALESCE(SUM(p.monto_operacion), 0)) AS monto_facturado,
    COALESCE(SUM(p.monto_operacion), 0)                     AS total_pagado,
    f.saldo_factura                                          AS diferencia,
    CASE
        WHEN COALESCE(SUM(p.monto_operacion), 0) = 0 THEN 'Sin pagos'
        WHEN f.saldo_factura = 0                     THEN 'Conciliada'
        ELSE 'Pago incompleto'
    END AS estatus_conciliacion
FROM factura f
JOIN usuario u ON u.id_usuario = f.id_usuario
LEFT JOIN vista_pagos_unificados p
       ON p.numero_control_factura = f.numero_control
      AND p.id_usuario = f.id_usuario
GROUP BY f.numero_control, f.id_usuario, u.nombres, u.apellidos, f.fecha_emision, f.saldo_factura
ORDER BY estatus_conciliacion, f.numero_control;

-- ============================================================
-- REP-03 / HU-57 — Rentabilidad y Ocupación de Espacios
-- Ingresos y numero de reservas por espacio físico.
-- ============================================================
DROP VIEW IF EXISTS vista_rep03_rentabilidad_ocupacion;

CREATE VIEW vista_rep03_rentabilidad_ocupacion AS
WITH ingreso_solicitud AS (
    SELECT ec.id_solicitud,
           ec.id_usuario,
           SUM(ic.cantidad * ic.precio_unitario + ic.impuestos_ley) AS ingreso
    FROM estado_cuenta ec
    JOIN item_consumo ic ON ic.numero_folio = ec.numero_folio
    GROUP BY ec.id_solicitud, ec.id_usuario
)
SELECT
    ef.nombre_sede,
    ef.nombre_edificio,
    ef.id_espacio,
    ef.capacidad_maxima_aforo,
    COUNT(r.id_solicitud)          AS total_reservas,
    COALESCE(SUM(isol.ingreso), 0) AS ingresos_generados
FROM espacio_fisico ef
LEFT JOIN reserva r
       ON r.nombre_sede     = ef.nombre_sede
      AND r.nombre_edificio = ef.nombre_edificio
      AND r.id_espacio      = ef.id_espacio
LEFT JOIN ingreso_solicitud isol
       ON isol.id_solicitud = r.id_solicitud
      AND isol.id_usuario   = r.id_usuario
GROUP BY ef.nombre_sede, ef.nombre_edificio, ef.id_espacio, ef.capacidad_maxima_aforo
ORDER BY ingresos_generados DESC;


-- ============================================================
-- REP-04 / HU-58 — Evolución/Demografía de Beneficiarios
-- Agrupa beneficiarios por grupo etario.
-- ============================================================
DROP VIEW IF EXISTS vista_rep04_demografia_beneficiarios;

CREATE VIEW vista_rep04_demografia_beneficiarios AS
WITH clasificados AS (
    SELECT
        CASE
            WHEN EXTRACT(YEAR FROM age(fecha_nacimiento)) BETWEEN 0  AND 12 THEN '0 - 12 años'
            WHEN EXTRACT(YEAR FROM age(fecha_nacimiento)) BETWEEN 13 AND 25 THEN '13 - 25 años'
            ELSE '65+ años'
        END AS grupo_etario
    FROM beneficiario_familiar
    WHERE fecha_nacimiento IS NOT NULL
      AND (EXTRACT(YEAR FROM age(fecha_nacimiento)) BETWEEN 0 AND 25
           OR EXTRACT(YEAR FROM age(fecha_nacimiento)) >= 65)
)
SELECT
    grupo_etario,
    CASE grupo_etario
        WHEN '0 - 12 años'  THEN 'Planes Vacacionales'
        WHEN '13 - 25 años' THEN 'Pólizas de Salud HCM'
        ELSE 'Fondo de Jubilación'
    END        AS beneficio_asociado,
    COUNT(*)   AS registrados
FROM clasificados
GROUP BY grupo_etario
ORDER BY MIN(CASE grupo_etario
        WHEN '0 - 12 años' THEN 1 WHEN '13 - 25 años' THEN 2 ELSE 3 END);


-- ============================================================
-- REP-05 / HU-59 — Efectividad e Inserción en Bolsa de Trabajo
-- ============================================================
DROP VIEW IF EXISTS vista_rep05_detalle_colocaciones;
DROP VIEW IF EXISTS vista_rep05_insercion_por_empresa;
DROP VIEW IF EXISTS vista_rep05_insercion_laboral;

CREATE VIEW vista_rep05_insercion_laboral AS
WITH egresados AS (SELECT DISTINCT id_usuario FROM egresado),
     colocados AS (SELECT DISTINCT id_usuario FROM toma)
SELECT
    (SELECT COUNT(*) FROM egresados)                          AS total_egresados,
    (SELECT COUNT(*) FROM colocados)                          AS egresados_colocados,
    (SELECT COUNT(*) FROM egresados)
        - (SELECT COUNT(*) FROM colocados)                    AS egresados_sin_colocar,
    ROUND(100.0 * (SELECT COUNT(*) FROM colocados)
                / NULLIF((SELECT COUNT(*) FROM egresados), 0), 1) AS tasa_insercion_pct;

CREATE VIEW vista_rep05_insercion_por_empresa AS
SELECT
    ae.razon_social,
    COUNT(DISTINCT ol.codigo_vacante)                    AS vacantes_publicadas,
    COUNT(DISTINCT ol.codigo_vacante)
        FILTER (WHERE ol.estatus_vacante = 'Finalizada') AS vacantes_cubiertas,
    COUNT(t.codigo_vacante)                              AS egresados_colocados
FROM aliado_externo ae
JOIN oportunidad_laboral ol ON ol.id_entidad = ae.id_entidad
LEFT JOIN toma t            ON t.id_entidad = ol.id_entidad
                           AND t.codigo_vacante = ol.codigo_vacante
GROUP BY ae.razon_social
ORDER BY egresados_colocados DESC;

CREATE VIEW vista_rep05_detalle_colocaciones AS
SELECT
    u.cedula,
    u.nombres || ' ' || u.apellidos AS egresado,
    e.titulo_obtenido,
    e.indice_academico_final,
    ae.razon_social                 AS empresa,
    t.codigo_vacante,
    ol.perfil_buscado,
    ol.fecha_oferta
FROM toma t
JOIN egresado e             ON e.id_usuario = t.id_usuario AND e.fecha_inicio = t.fecha_inicio
JOIN usuario u              ON u.id_usuario = e.id_usuario
JOIN oportunidad_laboral ol ON ol.id_entidad = t.id_entidad AND ol.codigo_vacante = t.codigo_vacante
JOIN aliado_externo ae      ON ae.id_entidad = t.id_entidad
ORDER BY ae.razon_social, u.apellidos;


-- ============================================================
-- REP-06 — Auditoría de Seguridad Global
-- ============================================================
DROP VIEW IF EXISTS vista_rep06_auditoria_seguridad;

CREATE VIEW vista_rep06_auditoria_seguridad AS
SELECT
    u.cedula,
    u.nombres,
    u.apellidos,
    u.correo_institucional,
    u.estado_cuenta,
    u.conteo_intentos_fallidos,
    u.estatus_verificacion_dospasos,
    u.ultima_conexion,
    hs.fecha_hora_acceso,
    hs.direccion_ip,
    hs.geolocalizacion_aproximada,
    hs.uuid_dispositivo
FROM usuario AS u
JOIN historial_sesion AS hs ON hs.id_usuario = u.id_usuario
ORDER BY hs.fecha_hora_acceso DESC;


-- ============================================================
-- REP-07 — Estados de Cuenta y Consumos
-- ============================================================
DROP VIEW IF EXISTS vista_rep07_estados_cuenta;

CREATE VIEW vista_rep07_estados_cuenta AS
SELECT
    u.cedula,
    u.nombres,
    u.apellidos,
    ec.numero_folio,
    ic.numero_linea,
    ic.concepto,
    ic.cantidad,
    ic.precio_unitario,
    ic.impuestos_ley,
    (ic.cantidad * ic.precio_unitario + ic.impuestos_ley) AS total_linea
FROM usuario AS u
JOIN estado_cuenta AS ec ON ec.id_usuario = u.id_usuario
JOIN item_consumo  AS ic ON ic.numero_folio = ec.numero_folio
ORDER BY ec.numero_folio, ic.numero_linea;


-- ============================================================
-- REP-08 — Perfil Histórico de Recurrencia
-- Frecuencia de uso y fidelidad por usuario.
-- ============================================================
DROP VIEW IF EXISTS vista_rep08_perfil_recurrencia;

CREATE VIEW vista_rep08_perfil_recurrencia AS
SELECT
    u.id_usuario,
    u.cedula,
    u.nombres || ' ' || u.apellidos          AS nombre_completo,
    u.correo_institucional,
    u.estado_cuenta,
    u.categoria_fidelidad,
    u.indice_recurrencia,
    COALESCE(ses.total_sesiones, 0)          AS total_sesiones,
    COALESCE(sol.total_solicitudes, 0)       AS total_solicitudes,
    COALESCE(to_char(ses.primera_sesion,    'DD/MM/YYYY HH24:MI'), '—') AS primera_sesion,
    COALESCE(to_char(ses.ultima_sesion,     'DD/MM/YYYY HH24:MI'), '—') AS ultima_sesion,
    COALESCE(to_char(sol.primera_solicitud, 'DD/MM/YYYY HH24:MI'), '—') AS primera_solicitud,
    COALESCE(to_char(sol.ultima_solicitud,  'DD/MM/YYYY HH24:MI'), '—') AS ultima_solicitud,
    (CURRENT_DATE - ses.ultima_sesion::date) AS dias_desde_ultima_sesion,
    CASE
        WHEN ses.ultima_sesion IS NULL THEN 'Nunca ha ingresado'
        ELSE 'Con actividad'
    END                                      AS estatus_actividad
FROM usuario u
LEFT JOIN (
    SELECT id_usuario,
           COUNT(*)               AS total_sesiones,
           MIN(fecha_hora_acceso) AS primera_sesion,
           MAX(fecha_hora_acceso) AS ultima_sesion
    FROM historial_sesion
    GROUP BY id_usuario
) AS ses ON ses.id_usuario = u.id_usuario
LEFT JOIN (
    SELECT id_usuario,
           COUNT(*)                 AS total_solicitudes,
           MIN(fecha_hora_apertura) AS primera_solicitud,
           MAX(fecha_hora_apertura) AS ultima_solicitud
    FROM solicitud_servicio
    GROUP BY id_usuario
) AS sol ON sol.id_usuario = u.id_usuario
ORDER BY u.indice_recurrencia DESC, total_sesiones DESC;
