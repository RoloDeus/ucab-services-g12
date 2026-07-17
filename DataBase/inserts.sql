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


--------------------------------------------------------------- Inserts (paola)------------------------------------------------------------------------------------

-- ============================================================
-- 1) USUARIOS (Información Base de Pruebas)
-- ============================================================
INSERT INTO public.usuario
    (cedula, nombres, apellidos, fecha_nacimiento, genero, calle, zona, ciudad,
     telefono, correo_institucional, contrasena, indice_recurrencia, categoria_fidelidad, ultima_conexion)
VALUES
 ('31123456','Ana','Rangel','1999-03-12','F','Av 1','Montalbán','Caracas','04141234567','ana.rangel@ucab.edu.ve','x123!', 42,'Preferencial','2026-06-20 09:00:00'),
 ('31234567','Bruno','Salas','2000-07-08','M','Av 2','Montalbán','Caracas','04141768114','bruno.salas@ucab.edu.ve','B123*', 18,'Frecuente','2026-06-15 14:30:00'),
 ('30234567','Carla','Méndez','1998-11-25','F','Av 3','Montalbán','Caracas','04246770486','carla.mendez@ucab.edu.ve','Hola123', 4,'Regular','2026-04-02 11:10:00'),
 ('32456789','Diego','Pérez','2001-01-30','M','Av 4','Montalbán','Caracas','04126589112','diego.perez@ucab.edu.ve','Hello1234*', 0,'Regular', NULL),
 ('31555888','Elena','Torres','2000-05-05','F','Av 5','Montalbán','Caracas','04149998877','elena.torres@ucab.edu.ve','x', 0,'Regular', NULL),
 ('33111222','Laura','Gómez','2002-04-10','F','Av 7','Montalbán','Caracas','04141231234','laura.gomez@ucab.edu.ve','x', 0,'Regular', NULL);


-- ============================================================
-- 2) INFRAESTRUCTURA Y SEDES
-- ============================================================
INSERT INTO public.sede (nombre_sede) VALUES 
 ('Montalbán'),
 ('Guayana') 
ON CONFLICT DO NOTHING;

INSERT INTO public.categoria_servicio VALUES 
 ('Salud'),
 ('Trámites Académicos'),
 ('Alquiler de Espacios'),
 ('Servicios de Salud'),
 ('Cursos y Diplomados');

INSERT INTO public.define_limite VALUES 
 ('Montalbán', 'Salud', 50.00),
 ('Guayana', 'Salud', 25.00),
 ('Montalbán', 'Trámites Académicos', 500.00);

INSERT INTO public.edificacion VALUES 
 ('Montalbán', 'Edificio Aulas', 'Av Principal, Piso 1'),
 ('Guayana', 'Edificio Central', 'Av Guayana, Puerto Ordaz'),
 ('Guayana', 'Laboratorioa', 'Escalera 3, Piso 1, Salon 1'),
 ('Montalbán', 'Laboratorios', 'Escalera 3, Piso 1, Salon 1'),
 ('Montalbán', 'Cincuentenario', 'Piso 1, Ala Sur'),
 ('Montalbán', 'Aulas', 'Planta Baja'),
 ('Montalbán', 'Modulos', 'piso 5, ala izquierda'),
 ('Montalbán', 'Laboratorios', 'Ala Sur');

INSERT INTO public.espacio_fisico VALUES 
 ('Montalbán', 'Edificio Aulas', 'Av Principal, Piso 1', 'AULA-101', 30, 'Pupitres', 'Operativo', 'Disponible'),
 ('Montalbán', 'Edificio Aulas', 'Av Principal, Piso 1', 'AUD-PB', 120, 'Butacas', 'Operativo', 'Disponible'),
 ('Montalbán', 'Edificio Aulas', 'Av Principal, Piso 1', 'LAB-202', 25, 'Computadoras', 'Operativo', 'Disponible'),
 ('Guayana', 'Edificio Central', 'Av Guayana, Puerto Ordaz', 'SALA-G1', 40, 'Mesas de reunión', 'Operativo', 'Disponible'),
 ('Guayana', 'Laboratorioa', 'Escalera 3, Piso 1, Salon 1', 'LAB-311', 30, 'Pupitres y proyector', 'En Reparación', 'Ocupado'),
 ('Montalbán', 'Cincuentenario', 'Piso 1, Ala Sur', 'Laboratorio Mac', 25, 'Mesones y Computadoras', 'Operativo', 'Disponible'),
 ('Montalbán', 'Aulas', 'Planta Baja', 'Aula 101', 40, 'Pupitres Universitarios', 'Operativo', 'Disponible'),
 ('Montalbán', 'Modulos', 'piso 5, ala izquierda', 'A5-44', 20, 'pupitres y meson', 'Operativo', 'Disponible');


-- ============================================================
-- 3) ENTIDADES PRESTADORAS Y ALIADOS
-- ============================================================
INSERT INTO public.entidad_prestadora VALUES 
 (1, 'Interno'),
 (2, 'Externo'),
 (3, 'Externo'),
 (4, 'Externo'),
 (100, 'Externo'),
 (200, 'Interno');

INSERT INTO public.prestador_interno (id_entidad, codigo_presupuestario, director_oficina) VALUES
 (1, 'PRE-MED-044', 'Dr. Carlos Mendoza');

INSERT INTO public.aliado_externo VALUES 
 (2, 'J-40555666-8', 'TechSolutions C.A.', '2028-12-15', 'vizquierdo@techsol.com'),
 (3, 'J-30999111-2', 'Feria de Comida Express', '2026-06-30', 'maria.alvarez@feria.com'),
 (4, 'J-50111222-3', 'Consultores Andinos C.A.', '2027-03-01', 'contacto@consultoresandinos.com'),
 (100, 'J-12345678-9', 'Empresas Polar C.A.', '2028-12-31', 'rrhh@empresaspolar.com');

INSERT INTO public.servicio_publicado (nombre_categoria, id_entidad, nombre_sede, descripcion_detallada, precio_base_institucional, ajuste_ubicacion)
SELECT 'Salud', pi.id_entidad, 'Montalbán', 'Consulta Cardiología', 40.00, 0.00
FROM public.prestador_interno pi WHERE pi.codigo_presupuestario = 'PRE-MED-044';

INSERT INTO public.oportunidad_laboral 
    (id_entidad, codigo_vacante, estatus_vacante, fecha_oferta, responsabilidades, beneficios, perfil_buscado)
VALUES
 ((SELECT id_entidad FROM public.aliado_externo WHERE rif='J-40555666-8'),'VAC-001','Finalizada','2026-01-10','Desarrollo Java/Spring','Seguro + bonos','Ingeniería Informática'),
 ((SELECT id_entidad FROM public.aliado_externo WHERE rif='J-30999111-2'),'VAC-002','Finalizada','2026-02-01','Gestión de local','Comisiones','Administración'),
 ((SELECT id_entidad FROM public.aliado_externo WHERE rif='J-40555666-8'),'VAC-003','Disponible','2026-03-01','Soporte técnico','Seguro','Ingeniería');


-- ============================================================
-- 4) HISTORIAL DE SESIONES Y VÍNCULOS
-- ============================================================
INSERT INTO public.historial_sesion (id_usuario, fecha_hora_acceso, direccion_ip, uuid_dispositivo, geolocalizacion_aproximada)
SELECT u.id_usuario, x.fha, x.ip, x.dev, x.geo
FROM public.usuario u
JOIN (VALUES
  ('31123456','2026-06-01 08:00:00'::timestamp,'10.0.0.1','dev-a','Caracas'),
  ('31123456','2026-06-10 09:30:00','10.0.0.1','dev-a','Caracas'),
  ('31123456','2026-06-20 09:00:00','190.202.12.34','dev-a','Caracas, Venezuela'),
  ('31234567','2026-05-20 10:00:00','10.0.0.2','dev-b','Valencia'),
  ('31234567','2026-06-15 14:30:00','10.0.0.2','dev-b','Valencia'),
  ('30234567','2026-04-02 11:10:00','10.0.0.3','dev-c','Maracay')
) AS x(ced, fha, ip, dev, geo) ON x.ced = u.cedula;

INSERT INTO public.periodo_vinculacion (id_usuario, rol_institucional, fecha_inicio, fecha_finalizacion)
SELECT u.id_usuario, x.rol, x.ini, NULL
FROM public.usuario u
JOIN (VALUES
  ('31123456','Egresado','2024-01-15'::date),
  ('31234567','Egresado','2023-01-15'),
  ('30234567','Egresado','2024-01-15'),
  ('32456789','Egresado','2025-01-15'),
  ('31123456','Profesor','2026-02-01'),
  ('31234567','Profesor','2026-02-01'),
  ('32456789','Estudiante','2026-02-01'),
  ('33111222','Estudiante','2026-02-01')
) AS x(ced, rol, ini) ON x.ced = u.cedula;


-- ============================================================
-- 5) SUBTIPOS DE USUARIOS (Egresados, Profesores, Estudiantes)
-- ============================================================
INSERT INTO public.egresado VALUES 
 (1, '2024-01-15', 'Ingeniero en Informática', 2024, 17.50),
 (2, '2023-01-15', 'Licenciado en Administración', 2023, 15.00),
 (3, '2024-01-15', 'Abogado', 2024, 16.20),
 (4, '2025-01-15', 'Ingeniero Civil', 2025, 14.00),
 (18, '2014-09-15', 'Ingeniero Informático', 2019, 16.80);

INSERT INTO public.profesor (id_usuario, fecha_inicio, unidad_adscripcion_presupuestaria, cargo_administrativo)
SELECT u.id_usuario, '2026-02-01', x.unidad, x.cargo
FROM public.usuario u
JOIN (VALUES
  ('31234567','Facultad de Ingeniería','Profesor Asociado'),
  ('31123456','Facultad de Derecho','Profesor Instructor')
) AS x(ced, unidad, cargo) ON x.ced = u.cedula;

INSERT INTO public.estudiante VALUES 
 (13, '2026-07-11', 0.00, 0, 1, 'Por Asignar', 'Por Asignar'),
 (14, '2026-07-11', 0.00, 0, 1, 'Por Asignar', 'Por Asignar'),
 (15, '2026-07-11', 0.00, 0, 1, 'Por Asignar', 'Por Asignar'),
 (16, '2021-09-15', 17.50, 120, 6, 'Ingeniería', 'Ingeniería Informática'),
 (6, '2026-02-01', 15.00, 120, 6, 'Ingeniería', 'Ingeniería Industrial'),
 (24, '2026-07-16', 0.00, 0, 1, 'Por Asignar', 'Por Asignar'),
 (4, '2026-02-01', 18.00, 120, 5, 'Ingeniería', 'Ingeniería Informática'),
 (7, '2026-07-11', 15.00, 120, 5, 'Ingeniería', 'Ingeniería Informática'),
 (23, '2026-07-14', 15.00, 125, 6, 'Ingeniería', 'Ingeniería Informática');


-- ============================================================
-- 6) ENTIDADES COMPLEMENTARIAS (Becas, Acreditaciones, TAI)
-- ============================================================
INSERT INTO public.acreditacion VALUES 
 (1, 'Documento Digital', 'Cédula de Identidad Vigente'),
 (2, 'Condición Académica', 'Estar Solvente Administrativamente');

INSERT INTO public.beca VALUES 
 (6, '2026-02-01', 'Excelencia', 'Activo', true),
 (16, '2021-09-15', 'Excelencia', 'Activo', true),
 (4, '2026-02-01', 'Excelencia', 'Activo', true),
 (7, '2026-07-11', 'Excelencia', 'En Evaluación', false),
 (23, '2026-07-14', 'Excelencia', 'En Evaluación', false);

INSERT INTO public.billetera_virtual_tai VALUES 
 ('TAI-CHIP-998877', 1, 30.00);


-- ============================================================
-- 7) SECCIONES, INSCRIPCIONES Y COLOCACIONES
-- ============================================================
INSERT INTO public.curso_seccion VALUES 
 ('INF-2012', 'Base de Datos'),
 ('DER-1001', 'Derecho Romano'),
 ('DERE-70', 'Derecho Adminitrativo II'),
 ('IN4411', 'Bases de Datos I'),
 ('IN5522', 'Ingeniería de Software'),
 ('AD1101', 'Administración Financiera'),
 ('AR5586', 'Administracion de Redes');

INSERT INTO public.imparte (id_profesor, fecha_inicio_profesor, codigo_curso)
SELECT u.id_usuario, '2026-02-01', x.curso
FROM public.usuario u
JOIN (VALUES ('31234567','INF-2012'), ('31123456','DER-1001')) AS x(ced, curso) ON x.ced = u.cedula;

INSERT INTO public.inscribe (id_estudiante, fecha_inicio_estudiante, codigo_curso)
SELECT u.id_usuario, '2026-02-01', x.curso
FROM public.usuario u
JOIN (VALUES ('32456789','INF-2012'), ('33111222','DER-1001')) AS x(ced, curso) ON x.ced = u.cedula;

INSERT INTO public.toma (id_usuario, fecha_inicio, id_entidad, codigo_vacante)
SELECT e.id_usuario, e.fecha_inicio, ol.id_entidad, ol.codigo_vacante
FROM (VALUES ('31123456','VAC-001'), ('31234567','VAC-002')) AS x(ced, vac)
JOIN public.usuario u             ON u.cedula = x.ced
JOIN public.egresado e            ON e.id_usuario = u.id_usuario
JOIN public.oportunidad_laboral ol ON ol.codigo_vacante = x.vac;


-- ============================================================
-- 8) NÚCLEO FAMILIAR Y ASOCIADOS
-- ============================================================
INSERT INTO public.beneficiario_familiar VALUES 
 (30000003, 1, 'Pedro', 'Rangel', 'Cónyuge', 'No Aplica', 'No Aplica', 'No Aplica', 'Entregado', '1985-03-20', 'Activo'),
 (30000002, 1, 'Sofía', 'Rangel', 'Hija', 'Entregado', 'Pendiente', 'No Aplica', 'No Aplica', '2015-09-01', 'Activo'),
 (30000001, 1, 'Luis', 'Rangel', 'Hijo', 'Entregado', 'Entregado', 'No Aplica', 'No Aplica', '2021-05-10', 'Activo'),
 (30000005, 2, 'Rosa', 'Salas', 'Madre', 'No Aplica', 'No Aplica', 'No Aplica', 'No Aplica', '1957-02-11', 'Archivado'),
 (30000004, 2, 'Marta', 'Salas', 'Hija', 'No Aplica', 'No Aplica', 'Pendiente', 'Pendiente', '2005-07-15', 'Activo'),
 (30000006, 3, 'Tomás', 'Méndez', 'Hijo', 'No Aplica', 'No Aplica', 'Pendiente', 'Pendiente', '2008-06-26', 'Activo'),
 (30444555, 17, 'Valentina', 'Perez', 'Hija', 'No Aplica', 'No Aplica', 'Pendiente', 'Pendiente', '2000-10-20', 'Activo'),
 (5111222, 19, 'Patricia', 'Sanchez', 'Madre', 'No Aplica', 'No Aplica', 'Pendiente', 'Pendiente', '1941-06-05', 'Activo');

INSERT INTO public.historial_fechas_cobertura (ci_familiar, fecha_inicio, fecha_fin, estatus_lapso) VALUES
  (30000001,'2025-01-01', NULL,        'Activo'),
  (30000002,'2025-01-01', NULL,        'Activo'),
  (30000003,'2025-01-01', NULL,        'Activo'),
  (30000004,'2025-01-01', NULL,        'Activo'),
  (30000005,'2023-01-01','2024-01-01', 'Vencido');

INSERT INTO public.acompanante_temporal VALUES 
 (31080772, 1, 'Lucía', 'Invitada', 'Activo', '2026-06-01'),
 (31065234, 1, 'Pedro', 'Visitante', 'Archivado', '2023-01-01'),
 (15925352, 23, 'maria', 'perez', 'Activo', '2026-07-14'),
 (15601098, 17, 'Valentina', 'Sanchez', 'Activo', '2026-07-16'),
 (30927897, 19, 'Pablo', 'Perez', 'Activo', '2026-07-16'),
 (30302111, 16, 'eduardo', 'sanchez', 'Activo', '2026-07-16'),
 (31222555, 4, 'carlota', 'carrero', 'Activo', '2026-07-16');


-- ============================================================
-- 9) TRANSACCIONES Y SOLICITUDES DE SERVICIO
-- ============================================================
INSERT INTO public.solicitud_servicio (id_usuario, fecha_hora_apertura, fecha_hora_cierre)
SELECT u.id_usuario, x.ap, x.ci
FROM public.usuario u
JOIN (VALUES
  ('2026-05-01 08:00:00'::timestamp,'2026-05-20 16:00:00'::timestamp), 
  ('2026-05-02 08:00:00', NULL),                                       
  ('2026-05-03 08:00:00', NULL),                                       
  ('2026-05-04 08:00:00', NULL),                                       
  ('2026-05-05 08:00:00','2026-05-05 12:00:00'),                       
  ('2026-07-01 09:00:00','2026-07-01 11:00:00'),                       
  ('2026-07-02 09:00:00','2026-07-02 11:00:00'),                       
  ('2026-07-03 09:00:00','2026-07-03 11:00:00'),                       
  ('2026-08-15 09:00:00', NULL)                                        
) AS x(ap, ci) ON TRUE
WHERE u.cedula = '31123456';

INSERT INTO public.solicitud_servicio (id_usuario, fecha_hora_apertura, fecha_hora_cierre)
SELECT id_usuario, '2026-06-15 15:00:00', '2026-06-15 16:00:00'
FROM public.usuario WHERE cedula = '31234567';

INSERT INTO public.solicitud_servicio (id_usuario, fecha_hora_apertura, fecha_hora_cierre)
SELECT id_usuario, '2026-04-02 11:30:00', '2026-04-02 12:00:00'
FROM public.usuario WHERE cedula = '30234567';


-- ============================================================
-- 10) ESTADOS DE CUENTA (Múltiples Módulos)
-- ============================================================
INSERT INTO public.estado_cuenta VALUES 
 (1, 2, 1), (2, 3, 1), (3, 4, 1), (4, 5, 1), (5, 6, 1), (6, 7, 1), (7, 8, 1), (8, 9, 1), (9, 13, 1), 
 (11, 18, 2), (12, 19, 3), (13, 20, 4), (14, 21, 1), (15, 24, 4), (16, 25, 5), (17, 26, 5), (18, 27, 1), 
 (19, 28, 1), (20, 29, 1), (21, 30, 2), (22, 31, 2), (23, 32, 2), (24, 33, 3), (25, 34, 3), (26, 35, 3), 
 (27, 36, 4), (28, 37, 4), (29, 38, 4), (30, 39, 5), (31, 40, 5), (32, 41, 5), (33, 42, 6), (34, 43, 6), 
 (35, 44, 6), (36, 45, 7), (37, 46, 7), (38, 47, 7), (39, 48, 13), (40, 49, 13), (41, 50, 13), (42, 51, 14), 
 (43, 52, 14), (44, 53, 14), (45, 54, 18), (46, 55, 18), (47, 56, 18), (48, 57, 19), (49, 58, 19), (50, 59, 19), 
 (51, 60, 15), (52, 61, 15), (53, 62, 15), (54, 63, 23), (55, 64, 23), (56, 65, 23), (57, 66, 16), (58, 67, 16), 
 (59, 68, 16), (60, 69, 17), (61, 70, 17), (62, 71, 17), (63, 72, 1), (64, 73, 1), (65, 74, 1), (66, 75, 2), 
 (67, 76, 2), (68, 77, 2), (69, 78, 3), (70, 79, 3), (71, 80, 3), (72, 81, 4), (73, 82, 4), (74, 83, 4), 
 (75, 84, 5), (76, 85, 5), (77, 86, 5), (81, 90, 7), (82, 91, 7), (83, 92, 7), (84, 93, 13), (85, 94, 13), 
 (86, 95, 13), (87, 96, 14), (88, 97, 14), (89, 98, 14), (90, 99, 15), (91, 100, 15), (92, 101, 15), (93, 102, 16), 
 (94, 103, 16), (95, 104, 16), (96, 105, 17), (97, 106, 17), (98, 107, 17), (99, 108, 18), (100, 109, 18), 
 (101, 110, 18), (102, 111, 19), (103, 112, 19), (104, 113, 19), (105, 114, 23), (106, 115, 23), (107, 116, 23), 
 (108, 117, 24), (109, 118, 24), (110, 119, 24);

-- Inserción asistida por subconsultas para folios específicos
INSERT INTO public.estado_cuenta (id_solicitud, id_usuario)
SELECT s.id_solicitud, s.id_usuario
FROM public.solicitud_servicio s
JOIN public.usuario u ON u.id_usuario = s.id_usuario AND u.cedula='31123456'
WHERE s.fecha_hora_apertura IN (
  '2026-05-02 08:00:00','2026-05-03 08:00:00','2026-05-04 08:00:00',
  '2026-05-05 08:00:00','2026-07-01 09:00:00','2026-07-02 09:00:00',
  '2026-07-03 09:00:00','2026-08-15 09:00:00'
) ON CONFLICT DO NOTHING;


-- ============================================================
-- 11) ÍTEMS DE CONSUMO, PASOS Y RESERVAS ACTIVAS
-- ============================================================
INSERT INTO public.item_consumo (numero_folio, numero_linea, concepto, cantidad, precio_unitario, impuestos_ley)
SELECT ec.numero_folio, 1, x.concepto, 1, x.precio, 0
FROM public.estado_cuenta ec
JOIN public.solicitud_servicio s ON s.id_solicitud=ec.id_solicitud AND s.id_usuario=ec.id_usuario
JOIN public.usuario u ON u.id_usuario=s.id_usuario AND u.cedula='31123456'
JOIN (VALUES
  ('2026-05-02 08:00:00'::timestamp,'Cargo folio 1',100.00),
  ('2026-05-03 08:00:00','Cargo folio 2',200.00),
  ('2026-05-04 08:00:00','Cargo folio 3',300.00)
) AS x(ap, concepto, precio) ON x.ap = s.fecha_hora_apertura;

INSERT INTO public.item_consumo (numero_folio, numero_linea, concepto, cantidad, precio_unitario, impuestos_ley)
SELECT ec.numero_folio, x.linea, x.concepto, 1, x.precio, x.imp
FROM public.estado_cuenta ec
JOIN public.solicitud_servicio s ON s.id_solicitud=ec.id_solicitud AND s.id_usuario=ec.id_usuario
JOIN public.usuario u ON u.id_usuario=s.id_usuario AND u.cedula='31123456'
JOIN (VALUES (1,'Derechos de Secretaría',100.00,0.00),(2,'Alquiler de Toga y Birrete',43.10,6.90)) AS x(linea,concepto,precio,imp) ON TRUE
WHERE s.fecha_hora_apertura = '2026-05-05 08:00:00';

INSERT INTO public.item_consumo (numero_folio, numero_linea, concepto, cantidad, precio_unitario, impuestos_ley)
SELECT ec.numero_folio, 1, x.concepto, 1, x.precio, 0
FROM public.estado_cuenta ec
JOIN public.solicitud_servicio s ON s.id_solicitud=ec.id_solicitud AND s.id_usuario=ec.id_usuario
JOIN public.usuario u ON u.id_usuario=s.id_usuario AND u.cedula='31123456'
JOIN (VALUES
  ('2026-07-01 09:00:00'::timestamp,'Alquiler Aula 101',200.00),
  ('2026-07-02 09:00:00','Alquiler Aula 101',150.00),
  ('2026-07-03 09:00:00','Alquiler Auditorio',500.00)
) AS x(ap, concepto, precio) ON x.ap = s.fecha_hora_apertura;

INSERT INTO public.item_consumo (numero_folio, numero_linea, concepto, cantidad, precio_unitario, impuestos_ley)
SELECT ec.numero_folio, x.linea, x.concepto, 1, x.precio, 0
FROM public.estado_cuenta ec
JOIN public.solicitud_servicio s ON s.id_solicitud=ec.id_solicitud AND s.id_usuario=ec.id_usuario
JOIN public.usuario u ON u.id_usuario=s.id_usuario AND u.cedula='31123456'
JOIN (VALUES (1,'Derechos de trámite',200.00),(2,'Material',50.00)) AS x(linea,concepto,precio) ON TRUE
WHERE s.fecha_hora_apertura = '2026-08-15 09:00:00';

INSERT INTO public.paso_actividad
    (id_solicitud, id_usuario, secuencia_paso, estado_paso, oficina_responsable, fecha_hora_finalizacion_exacta)
SELECT s.id_solicitud, s.id_usuario, x.sec, 'Completado', x.oficina, x.fin
FROM public.solicitud_servicio s
JOIN public.usuario u ON u.id_usuario = s.id_usuario AND u.cedula = '31123456'
JOIN (VALUES
  (1,'Escuela de Origen','2026-05-03 10:00:00'::timestamp),
  (2,'Secretaría',       '2026-05-17 10:00:00'),
  (3,'Caja Central',     '2026-05-18 16:00:00'),
  (4,'Rectorado',        '2026-05-20 09:00:00')
) AS x(sec, oficina, fin) ON TRUE
WHERE s.fecha_hora_apertura = '2026-05-01 08:00:00';

INSERT INTO public.reserva (id_solicitud, id_usuario, nombre_sede, nombre_edificio, id_espacio, fecha_reserva, bloque_horario_solicitado)
SELECT s.id_solicitud, s.id_usuario, 'Montalbán','Edificio Aulas', x.espacio, x.fecha, x.bloque
FROM public.solicitud_servicio s
JOIN public.usuario u ON u.id_usuario = s.id_usuario AND u.cedula = '31123456'
JOIN (VALUES
  ('2026-07-01 09:00:00'::timestamp,'AULA-101','2026-07-10'::date,'08:00-10:00'),
  ('2026-07-02 09:00:00','AULA-101','2026-07-11','10:00-12:00'),
  ('2026-07-03 09:00:00','AUD-PB', '2026-07-12','14:00-16:00')
) AS x(ap, espacio, fecha, bloque) ON x.ap = s.fecha_hora_apertura;


-- ============================================================
-- 12) FACTURACIÓN CONSOLIDADA
-- ============================================================
INSERT INTO public.factura VALUES 
 ('FAC-201', 2, 11, '2026-07-10', 75.00),
 ('FAC-202', 3, 12, '2026-07-10', 150.00),
 ('FAC-203', 4, 13, '2026-07-10', 220.00),
 ('FAC-001', 1, 1, '2026-05-10', 0.00),
 ('FAC-AUTO-6', 1, 5, '2026-07-11', 200.00),
 ('FAC-AUTO-7', 1, 6, '2026-07-11', 150.00),
 ('FAC-AUTO-8', 1, 7, '2026-07-11', 500.00),
 ('FAC-AUTO-9', 1, 14, '2026-07-11', 31.00),
 ('FAC-AUTO-10', 4, 15, '2026-07-11', 180.00),
 ('FAC-AUTO-11', 5, 16, '2026-07-11', 45.00),
 ('FAC-AUTO-12', 5, 17, '2026-07-11', 45.00),
 ('FAC-003', 1, 3, '2026-05-10', 0.00),
 ('FAC-002', 1, 2, '2026-05-10', 0.00),
 ('FAC-301', 1, 18, '2026-07-16', 120.00),
 ('FAC-302', 1, 19, '2026-07-16', 85.00),
 ('FAC-303', 1, 20, '2026-07-16', 200.00),
 ('FAC-304', 2, 21, '2026-07-16', 95.00),
 ('FAC-305', 2, 22, '2026-07-16', 150.00),
 ('FAC-306', 2, 23, '2026-07-16', 60.00),
 ('FAC-307', 3, 24, '2026-07-16', 175.00),
 ('FAC-308', 3, 25, '2026-07-16', 110.00),
 ('FAC-309', 3, 26, '2026-07-16', 90.00),
 ('FAC-310', 4, 27, '2026-07-16', 130.00),
 ('FAC-311', 4, 28, '2026-07-16', 75.00),
 ('FAC-312', 4, 29, '2026-07-16', 220.00),
 ('FAC-313', 5, 30, '2026-07-16', 100.00),
 ('FAC-314', 5, 31, '2026-07-16', 45.00),
 ('FAC-315', 5, 32, '2026-07-16', 180.00),
 ('FAC-316', 6, 33, '2026-07-16', 65.00),
 ('FAC-317', 6, 34, '2026-07-16', 140.00),
 ('FAC-318', 6, 35, '2026-07-16', 95.00),
 ('FAC-319', 7, 36, '2026-07-16', 160.00),
 ('FAC-320', 7, 37, '2026-07-16', 80.00),
 ('FAC-321', 7, 38, '2026-07-16', 115.00),
 ('FAC-322', 13, 39, '2026-07-16', 90.00),
 ('FAC-323', 13, 40, '2026-07-16', 135.00),
 ('FAC-324', 13, 41, '2026-07-16', 70.00),
 ('FAC-325', 14, 42, '2026-07-16', 200.00),
 ('FAC-326', 14, 43, '2026-07-16', 55.00),
 ('FAC-327', 14, 44, '2026-07-16', 125.00),
 ('FAC-328', 18, 45, '2026-07-16', 145.00),
 ('FAC-329', 18, 46, '2026-07-16', 85.00),
 ('FAC-330', 18, 47, '2026-07-16', 190.00),
 ('FAC-331', 19, 48, '2026-07-16', 110.00),
 ('FAC-332', 19, 49, '2026-07-16', 75.00),
 ('FAC-333', 19, 50, '2026-07-16', 160.00),
 ('FAC-334', 15, 51, '2026-07-16', 95.00),
 ('FAC-335', 15, 52, '2026-07-16', 130.00),
 ('FAC-336', 15, 53, '2026-07-16', 50.00),
 ('FAC-337', 23, 54, '2026-07-16', 175.00),
 ('FAC-338', 23, 55, '2026-07-16', 90.00),
 ('FAC-339', 23, 56, '2026-07-16', 120.00),
 ('FAC-340', 16, 57, '2026-07-16', 85.00),
 ('FAC-341', 16, 58, '2026-07-16', 140.00),
 ('FAC-342', 16, 59, '2026-07-16', 65.00),
 ('FAC-343', 17, 60, '2026-07-16', 100.00),
 ('FAC-344', 17, 61, '2026-07-16', 155.00),
 ('FAC-345', 17, 62, '2026-07-16', 80.00),
 ('FAC-401', 1, 63, '2026-07-16', 120.00),
 ('FAC-402', 1, 64, '2026-07-16', 85.00),
 ('FAC-403', 1, 65, '2026-07-16', 200.00),
 ('FAC-404', 2, 66, '2026-07-16', 95.00),
 ('FAC-405', 2, 67, '2026-07-16', 150.00),
 ('FAC-406', 2, 68, '2026-07-16', 60.00),
 ('FAC-407', 3, 69, '2026-07-16', 175.00),
 ('FAC-408', 3, 70, '2026-07-16', 110.00),
 ('FAC-409', 3, 71, '2026-07-16', 90.00),
 ('FAC-410', 4, 72, '2026-07-16', 130.00),
 ('FAC-411', 4, 73, '2026-07-16', 75.00),
 ('FAC-412', 4, 74, '2026-07-16', 220.00),
 ('FAC-413', 5, 75, '2026-07-16', 100.00),
 ('FAC-414', 5, 76, '2026-07-16', 45.00),
 ('FAC-415', 5, 77, '2026-07-16', 180.00),
 ('FAC-416', 6, 78, '2026-07-16', 65.00),
 ('FAC-417', 6, 79, '2026-07-16', 140.00),
 ('FAC-418', 6, 80, '2026-07-16', 95.00),
 ('FAC-419', 7, 81, '2026-07-16', 160.00),
 ('FAC-420', 7, 82, '2026-07-16', 80.00),
 ('FAC-421', 7, 83, '2026-07-16', 115.00),
 ('FAC-422', 13, 84, '2026-07-16', 90.00),
 ('FAC-423', 13, 85, '2026-07-16', 135.00),
 ('FAC-424', 13, 86, '2026-07-16', 70.00),
 ('FAC-425', 14, 87, '2026-07-16', 200.00),
 ('FAC-426', 14, 88, '2026-07-16', 55.00),
 ('FAC-427', 14, 89, '2026-07-16', 125.00),
 ('FAC-428', 15, 90, '2026-07-16', 145.00),
 ('FAC-429', 15, 91, '2026-07-16', 85.00),
 ('FAC-430', 15, 92, '2026-07-16', 190.00),
 ('FAC-431', 16, 93, '2026-07-16', 110.00),
 ('FAC-432', 16, 94, '2026-07-16', 75.00),
 ('FAC-433', 16, 95, '2026-07-16', 160.00),
 ('FAC-434', 17, 96, '2026-07-16', 95.00),
 ('FAC-435', 17, 97, '2026-07-16', 130.00),
 ('FAC-436', 17, 98, '2026-07-16', 50.00),
 ('FAC-437', 18, 99, '2026-07-16', 175.00),
 ('FAC-438', 18, 100, '2026-07-16', 90.00),
 ('FAC-439', 18, 101, '2026-07-16', 120.00),
 ('FAC-440', 19, 102, '2026-07-16', 85.00),
 ('FAC-441', 19, 103, '2026-07-16', 140.00),
 ('FAC-442', 19, 104, '2026-07-16', 65.00),
 ('FAC-443', 23, 105, '2026-07-16', 100.00),
 ('FAC-444', 23, 106, '2026-07-16', 155.00),
 ('FAC-445', 23, 107, '2026-07-16', 80.00),
 ('FAC-446', 24, 108, '2026-07-16', 120.00),
 ('FAC-447', 24, 109, '2026-07-16', 95.00),
 ('FAC-448', 24, 110, '2026-07-16', 170.00);


-- ============================================================
-- 13) PASARELAS DE PAGOS REGISTRADOS
-- ============================================================
INSERT INTO public.pago_zelle (id_pago, numero_control_factura, id_usuario, fecha_hora_pago, monto_operacion, canal_operacion, correo_origen, nombre_titular_emisor, codigo_confirmacion)
SELECT 'PZ-001','FAC-001', id_usuario,'2026-05-11 09:00',100.00,'Portal Digital','ana@banco.com','Ana Rangel','CONF-001'
FROM public.usuario WHERE cedula='31123456';

INSERT INTO public.pago_tarjeta (id_pago, numero_control_factura, id_usuario, fecha_hora_pago, monto_operacion, canal_operacion, numero_tarjeta, fecha_vencimiento, tipo_red, compania_emisora)
SELECT 'PT-001','FAC-002', id_usuario,'2026-05-12 10:00',120.00,'Taquilla Presencial','4598','11/28','Internacional','Mastercard'
FROM public.usuario WHERE cedula='31123456';


-- ============================================================
-- 14) MACROECONOMÍA (Indicadores cambiarios)
-- ============================================================
INSERT INTO public.tasa_cambio_bcv (fecha_hora_vigencia, monto_oficial) VALUES
 ('2026-06-20 09:00:00', 500.00),
 ('2026-06-24 09:00:00', 617.00);
