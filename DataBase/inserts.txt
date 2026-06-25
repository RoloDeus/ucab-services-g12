-- 1. Sedes de la Universidad 
INSERT INTO sede (nombre_sede) VALUES 
('Montalbán'),
('Guayana');

-- 2. Categorías de Servicios [cite: 4]
INSERT INTO categoria_servicio (nombre_categoria) VALUES 
('Trámites Académicos'),
('Alquiler de Espacios'),
('Servicios de Salud'),
('Cursos y Diplomados');

-- 3. Tasa de Cambio BCV [cite: 5]
INSERT INTO tasa_cambio_bcv (fecha_hora_vigencia, monto_oficial) VALUES 
('2026-06-20 09:00:00', 36.45),
('2026-06-21 09:00:00', 36.50),
('2026-06-22 09:00:00', 36.55);

-- 4. Acreditaciones Exigidas [cite: 29]
INSERT INTO acreditacion (tipo_acreditacion, nombre_acreditacion) VALUES 
('Documento Digital', 'Cédula de Identidad Vigente'),
('Condición Académica', 'Estar Solvente Administrativamente');

-- 5. Cursos y Secciones (Catálogo Académico) [cite: 15]
INSERT INTO curso_seccion (codigo_curso, nombre_materia) VALUES 
('IN4411', 'Bases de Datos I'),
('IN5522', 'Ingeniería de Software'),
('AD1101', 'Administración Financiera');


-- 1. Insertamos los Usuarios Base 
INSERT INTO usuario (id_usuario, cedula, nombres, apellidos, fecha_nacimiento, genero, calle, zona, ciudad, telefono, correo_institucional, contrasena, estado_cuenta, estatus_Verificacion_DosPasos, categoria_fidelidad) VALUES 
(1, 'V-28000111', 'Carlos', 'Mendoza', '2002-05-14', 'M', 'Av. Teherán', 'Montalbán', 'Caracas', '0414-1234567', 'cmendoza@est.ucab.edu.ve', 'hash123', 'Activa', TRUE, 'Frecuente'),
(2, 'V-15000222', 'Marlene', 'Goncalves', '1980-10-20', 'F', 'Calle 4', 'La Urbina', 'Caracas', '0412-7654321', 'mgoncalves@ucab.edu.ve', 'hash456', 'Activa', TRUE, 'Preferencial'),
(3, 'V-20000333', 'Luis', 'Pérez', '1996-02-10', 'M', 'Vereda 2', 'Unare', 'Puerto Ordaz', '0416-9998877', 'lperez@alumni.ucab.edu.ve', 'hash789', 'Activa', FALSE, 'Regular');

-- 2. Registro en Periodo de Vinculación (Tabla Madre de Herencia) [cite: 7, 8]
INSERT INTO periodo_vinculacion (id_usuario, rol_institucional, fecha_inicio, fecha_finalizacion) VALUES 
(1, 'Estudiante', '2021-09-15', NULL),
(2, 'Profesor', '2010-03-01', NULL),
(3, 'Egresado', '2014-09-15', '2019-07-20');

-- 3. Inserción en Tablas Hijas según el Rol [cite: 9, 10, 11, 14]
INSERT INTO estudiante (id_usuario, fecha_inicio, promedio_ponderado, uc_aprobadas, semestre_actual, facultad_adscripcion, escuela_adscripcion) VALUES 
(1, '2021-09-15', 17.50, 120, 6, 'Ingeniería', 'Ingeniería Informática');

INSERT INTO profesor (id_usuario, fecha_inicio, unidad_adscripcion_presupuestaria, codigo_investigador, cargo_administrativo) VALUES 
(2, '2010-03-01', 'Facultad de Ingeniería', 'INV-UCAB-001', 'Docente Titular');

INSERT INTO egresado (id_usuario, fecha_inicio, titulo_obtenido, año_graduacion, indice_academico_final) VALUES 
(3, '2014-09-15', 'Ingeniero Informático', 2019, 16.80);


-- 1. Relación Académica (Inscripciones e Imparticiones) [cite: 16, 17]
INSERT INTO inscribe (id_estudiante, fecha_inicio_estudiante, codigo_curso) VALUES 
(1, '2021-09-15', 'IN4411');

INSERT INTO imparte (id_profesor, fecha_inicio_profesor, codigo_curso) VALUES 
(2, '2010-03-01', 'IN4411');

-- 2. Beneficios Estudiantiles (Becas) [cite: 18]
INSERT INTO beca (id_usuario, fecha_inicio, tipo_beca, estatus_beneficio, cumplimiento_indice) VALUES 
(1, '2021-09-15', 'Excelencia', 'Activo', TRUE);

-- 3. Carga Familiar de la Profesora (Respetando los constraints de documentos) [cite: 20, 21]
INSERT INTO beneficiario_familiar (ci_familiar, id_usuario, nombres, apellidos, parentesco, fecha_nacimiento,esquema_vacunacion, centro_educacion_inicial, constancia_estudios_universitarios, certificado_solteria) VALUES 
(31222333, 2, 'Miguel', 'Goncalves', 'Hijo', '2008-06-23','Entregado', 'No Aplica', 'Pendiente', 'No Aplica');


-- 1. Edificaciones y Espacios Físicos [cite: 37, 38]
INSERT INTO edificacion (nombre_sede, nombre_edificio, direccion_interna) VALUES 
('Montalbán', 'Cincuentenario', 'Piso 1, Ala Sur'),
('Montalbán', 'Aulas', 'Planta Baja');

INSERT INTO espacio_fisico (nombre_sede, nombre_edificio, direccion_interna, id_espacio, capacidad_maxima_aforo, tipo_mobiliario, estado_mantenimiento, registro_disponibilidad) VALUES 
('Montalbán', 'Cincuentenario', 'Piso 1, Ala Sur', 'Laboratorio Mac', 25, 'Mesones y Computadoras', 'Operativo', 'Disponible'),
('Montalbán', 'Aulas', 'Planta Baja', 'Aula 101', 40, 'Pupitres Universitarios', 'Operativo', 'Disponible');

-- 2. Entidades Prestadoras y Alianzas (Bolsa de Trabajo) [cite: 30, 32, 45]
INSERT INTO entidad_prestadora (id_entidad, tipo_entidad) VALUES (100, 'Externo');

INSERT INTO aliado_externo (id_entidad, rif, razon_social, fecha_vencimiento_contrato, contactos_legales) VALUES 
(100, 'J-12345678-9', 'Empresas Polar C.A.', '2028-12-31', 'rrhh@empresaspolar.com');

INSERT INTO oportunidad_laboral (id_entidad, codigo_vacante, estatus_vacante, fecha_oferta, responsabilidades, beneficios, perfil_buscado) VALUES 
(100, 'VAC-DBA-01', 'Disponible', '2026-06-01', 'Administración de PostgreSQL', 'Seguro HCM, Bono de alimentación', 'Ingeniero Informático con 2 años de experiencia');

-- El Egresado toma la oferta laboral [cite: 46]
INSERT INTO toma (id_usuario, fecha_inicio, id_entidad, codigo_vacante) VALUES 
(3, '2014-09-15', 100, 'VAC-DBA-01');


-- 1. Solicitud de Servicio (Ej: Constancia de Notas) [cite: 24]
INSERT INTO solicitud_servicio (id_solicitud, id_usuario, fecha_hora_apertura, fecha_hora_cierre) VALUES 
(1, 1, '2026-06-22 10:00:00', NULL);

-- 2. Estado de Cuenta y Consumos [cite: 25, 27]
INSERT INTO estado_cuenta (numero_folio, id_solicitud, id_usuario) VALUES 
(1000, 1, 1);

INSERT INTO item_consumo (numero_folio, numero_linea, concepto, cantidad, precio_unitario, impuestos_ley) VALUES 
(1000, 1, 'Emisión de Constancia de Notas Certificada', 1, 15.00, 2.40); -- Total: 17.40 USD

-- 3. Facturación [cite: 47]
INSERT INTO factura (numero_control, id_usuario, numero_folio, fecha_emision, saldo_factura) VALUES 
('FAC-2026-0001', 1, 1000, '2026-06-22', 17.40);

-- 4. Billetera TAI para el Estudiante [cite: 49]
INSERT INTO billetera_virtual_tai (uid_chip, id_usuario, saldo_virtual) VALUES 
('TAI-CHIP-998877', 1, 50.00);

-- 5. Pagos (Simulamos que el estudiante paga con Zelle) [cite: 50, 51]
INSERT INTO pago_zelle (id_pago, numero_control_factura, id_usuario, fecha_hora_pago, monto_operacion, canal_operacion, correo_origen, nombre_titular_emisor, codigo_confirmacion) VALUES 
('PZ-001', 'FAC-2026-0001', 1, CURRENT_TIMESTAMP, 17.40, 'Portal Digital', 'carlos.mendoza@gmail.com', 'Carlos Mendoza', 'REF-ZELLE-987654');



-- 1. Crear el Usuario para el Administrativo
INSERT INTO usuario (id_usuario, cedula, nombres, apellidos, fecha_nacimiento, genero, calle, zona, ciudad, telefono, correo_institucional, contrasena, estado_cuenta)
VALUES (4, 'V-18000444', 'Roberto', 'García', '1988-07-12', 'M', 'Calle 2', 'Chacao', 'Caracas', '0414-2223344', 'rgarcia@ucab.edu.ve', 'hash999', 'Activa');

-- 2. Vincularlo y Herencia Administrativa (Carga horaria entre 1 y 44)
INSERT INTO periodo_vinculacion (id_usuario, rol_institucional, fecha_inicio, fecha_finalizacion)
VALUES (4, 'Personal Administrativo', '2018-01-15', NULL);

INSERT INTO personal_administrativo (id_usuario, fecha_inicio, cargo_administrativo, unidad_adscripcion_presupuestaria, carga_horaria_semanal)
VALUES (4, '2018-01-15', 'Coordinador de Servicios', 'Dirección de Administración', 40);

-- 3. Auditoría de Conexión (Usando al estudiante ID 1)
INSERT INTO historial_sesion (id_usuario, fecha_hora_acceso, direccion_ip, uuid_dispositivo, geolocalizacion_aproximada)
VALUES (1, '2026-06-23 08:30:00', '192.168.1.100', '550e8400-e29b-41d4-a716-446655440000', 'Caracas, Venezuela');

-- 4. Preparaduría del Estudiante
INSERT INTO preparaduria (id_usuario, fecha_inicio, asignatura_asignada, horas_ayudantia)
VALUES (1, '2021-09-15', 'Bases de Datos I', 8);


-- 1. Lapso de Cobertura del Hijo de la profesora (ci_familiar = 31222333)
INSERT INTO historial_fechas_cobertura (ci_familiar, fecha_inicio, fecha_fin, estatus_lapso)
VALUES (31222333, '2026-01-01', '2026-12-31', 'Activo');

-- 2. Acompañante Temporal del estudiante
INSERT INTO acompanante_temporal (ci_acompanante, id_usuario, nombres, apellidos)
VALUES (20555666, 1, 'Andrés', 'Mendoza');

-- 3. Pasos de la solicitud #1 (Secuencia obligatoria > 0 y estados válidos)
INSERT INTO paso_actividad (id_solicitud, id_usuario, secuencia_paso, estado_paso, oficina_responsable)
VALUES 
(1, 1, 1, 'Completado', 'Secretaría General'),
(1, 1, 2, 'En Proceso', 'Caja');


-- 1. Prestador Interno de Servicios (El coordinador que acabamos de crear)
INSERT INTO entidad_prestadora (id_entidad, tipo_entidad) VALUES (200, 'Interno');

INSERT INTO prestador_interno (id_entidad, codigo_presupuestario, director_oficina)
VALUES (200, 'PRE-UCAB-001', 'Roberto García');

-- 2. Catálogo: Servicio Publicado
INSERT INTO servicio_publicado (id_servicio, nombre_categoria, id_entidad, descripcion_detallada, precio_base_institucional, ajuste_ubicacion)
VALUES (10, 'Trámites Académicos', 200, 'Emisión de Constancia de Notas', 15.00, 0.00);

-- 3. Restricciones de Servicios (Exige acreditación 2 "Condición Académica", Tarifas y Cargos)
INSERT INTO exige (id_servicio, id_acreditacion) VALUES (10, 2);

INSERT INTO tarifa_diferenciada (id_servicio, perfil_solicitante, monto_tarifa)
VALUES (10, 'Estudiante', 15.00), (10, 'Egresado', 20.00);

INSERT INTO cargo_adicional (id_servicio, concepto_suplemento, monto_cargo)
VALUES (10, 'Impresión en Papel de Seguridad', 5.00);

INSERT INTO define_limite (nombre_sede, nombre_categoria, monto_limite_maximo)
VALUES ('Montalbán', 'Trámites Académicos', 500.00);

-- 4. Infraestructura: Recursos y Reservas
INSERT INTO recurso_tecnologico (nombre_sede, nombre_edificio, id_espacio, nombre_recurso)
VALUES ('Montalbán', 'Aulas', 'Aula 101', 'Video Beam Epson');

-- Solicitud base para poder reservar (Profesor ID 2)
INSERT INTO solicitud_servicio (id_solicitud, id_usuario, fecha_hora_apertura) VALUES (2, 2, '2026-06-23 09:00:00');

INSERT INTO reserva (id_solicitud, id_usuario, nombre_sede, nombre_edificio, id_espacio, fecha_reserva, bloque_horario_solicitado)
VALUES (2, 2, 'Montalbán', 'Aulas', 'Aula 101', '2026-07-01', '10:00 - 12:00');|	


-- 1. Generamos Solicitudes, Estados de Cuenta y Facturas en lote para los pagos
INSERT INTO solicitud_servicio (id_solicitud, id_usuario) VALUES (3, 1), (4, 1), (5, 1), (6, 1), (7, 1);
INSERT INTO estado_cuenta (numero_folio, id_solicitud, id_usuario) VALUES (1001, 3, 1), (1002, 4, 1), (1003, 5, 1), (1004, 6, 1), (1005, 7, 1);

INSERT INTO factura (numero_control, id_usuario, numero_folio, fecha_emision, saldo_factura) VALUES
('FAC-0002', 1, 1001, '2026-06-23', 50.00),
('FAC-0003', 1, 1002, '2026-06-23', 25.00), 
('FAC-0004', 1, 1003, '2026-06-23', 10.00), 
('FAC-0005', 1, 1004, '2026-06-23', 5.00),  
('FAC-0006', 1, 1005, '2026-06-23', 100.00); 

-- 2. Pago Criptomoneda (Validando Red TRC20)
INSERT INTO pago_criptomoneda (id_pago, numero_control_factura, id_usuario, fecha_hora_pago, monto_operacion, canal_operacion, hash_transaccion_txid, direccion_billetera_origen, red_utilizada, tasa_conversion_blockchain)
VALUES ('PCR-001', 'FAC-0002', 1, CURRENT_TIMESTAMP, 50.00, 'Portal Digital', '0xabc123...', '0xdef456...', 'TRC20', 1.00000000);

-- 3. Pago Tarjeta (Validando Tipo de Red Nacional/Internacional)
INSERT INTO pago_tarjeta (id_pago, numero_control_factura, id_usuario, fecha_hora_pago, monto_operacion, canal_operacion, numero_tarjeta, fecha_vencimiento, tipo_red, compania_emisora)
VALUES ('PT-001', 'FAC-0003', 1, CURRENT_TIMESTAMP, 25.00, 'Portal Digital', '4111222233334444', '12/28', 'Internacional', 'Visa');

-- 4. Pago Móvil
INSERT INTO pago_movil (id_pago, numero_control_factura, id_usuario, fecha_hora_pago, monto_operacion, canal_operacion, numero_telefono_emisor, banco_origen, numero_referencia, fecha_movimiento)
VALUES ('PM-001', 'FAC-0004', 1, CURRENT_TIMESTAMP, 10.00, 'Portal Digital', '04141234567', 'Mercantil', 'REF998877', CURRENT_DATE);

-- 5. Pago TAI (Conectando con la Billetera creada en la Fase 5)
INSERT INTO pago_tai (id_pago, numero_control_factura, id_usuario, fecha_hora_pago, monto_operacion, canal_operacion, uid_chip, codigo_terminal_pos, saldo_remanente)
VALUES ('PTAI-001', 'FAC-0005', 1, CURRENT_TIMESTAMP, 5.00, 'Taquilla Presencial', 'TAI-CHIP-998877', 'POS-CAJA-01', 45.00);

-- 6. Pago Efectivo y Desglose de Billetes (Moneda Divisas)
INSERT INTO pago_efectivo (id_pago, numero_control_factura, id_usuario, fecha_hora_pago, monto_operacion, canal_operacion, fecha_hora_vigencia_bcv, moneda_curso, monto_recibido)
VALUES ('PE-001', 'FAC-0006', 1, CURRENT_TIMESTAMP, 100.00, 'Taquilla Presencial', '2026-06-22 09:00:00', 'Divisas', 100.00);

INSERT INTO desglose_denominacion_efectivo (id_pago, denominacion_billete, cantidad_billetes)
VALUES ('PE-001', 'Billete 50 USD', 2);


SELECT setval('usuario_id_usuario_seq', (SELECT MAX(id_usuario) FROM usuario));

