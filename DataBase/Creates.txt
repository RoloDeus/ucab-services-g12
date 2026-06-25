CREATE TABLE usuario (
    id_usuario BIGSERIAL PRIMARY KEY,
    cedula VARCHAR(20) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    genero CHAR(1) NOT NULL,
    calle VARCHAR(150) NOT NULL,
    zona VARCHAR(150) NOT NULL,
    ciudad VARCHAR(150) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    correo_institucional VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    estado_cuenta VARCHAR(20) NOT NULL DEFAULT 'Activa',
    estatus_Verificacion_DosPasos BOOLEAN NOT NULL DEFAULT FALSE,
    conteo_intentos_fallidos INTEGER NOT NULL DEFAULT 0,
    fecha_cambio_clave TIMESTAMP,
    ultima_conexion TIMESTAMP,
    indice_recurrencia INTEGER NOT NULL DEFAULT 0,
    categoria_fidelidad VARCHAR(20) NOT NULL DEFAULT 'Regular',
   
    CONSTRAINT chk_usuario_genero CHECK (genero IN ('M', 'F')),
    CONSTRAINT chk_usuario_estado CHECK (estado_cuenta IN ('Activa', 'Suspendida', 'Bloqueada')),
    CONSTRAINT chk_usuario_intentos CHECK (conteo_intentos_fallidos >= 0),
    CONSTRAINT chk_usuario_recurrencia CHECK (indice_recurrencia >= 0),
    CONSTRAINT chk_usuario_fidelidad CHECK (categoria_fidelidad IN ('Regular', 'Frecuente', 'Preferencial'))
);

CREATE TABLE sede (
    nombre_sede VARCHAR(100) NOT NULL,
    PRIMARY KEY (nombre_sede)
);

CREATE TABLE categoria_servicio (
    nombre_categoria VARCHAR(100) NOT NULL,
    
    PRIMARY KEY (nombre_categoria)
);

CREATE TABLE tasa_cambio_bcv (
    fecha_hora_vigencia TIMESTAMP NOT NULL,
    monto_oficial NUMERIC(15,2) NOT NULL,
    
    PRIMARY KEY (fecha_hora_vigencia),
    CONSTRAINT chk_tasa_monto CHECK (monto_oficial > 0)
);


CREATE TABLE entidad_prestadora (
    id_entidad BIGSERIAL NOT NULL,
    tipo_entidad VARCHAR(50) NOT NULL, 
    
    PRIMARY KEY (id_entidad),
    CONSTRAINT chk_entidad_tipo CHECK (tipo_entidad IN ('Interno', 'Externo'))
);

CREATE TABLE historial_sesion (
    id_usuario BIGINT NOT NULL,
    fecha_hora_acceso TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    direccion_ip VARCHAR(45) NOT NULL, -- Valida formatos IPv4 e IPv6
    uuid_dispositivo VARCHAR(255) NOT NULL,
    geolocalizacion_aproximada VARCHAR(255) NOT NULL,

    PRIMARY KEY (id_usuario, fecha_hora_acceso),
    
    -- Restricciones de Integridad Referencial (Auditoría Inmutable)
    CONSTRAINT fk_historial_usuario FOREIGN KEY (id_usuario) 
        REFERENCES usuario(id_usuario) ON DELETE RESTRICT
);


CREATE TABLE periodo_vinculacion (
    id_usuario BIGINT NOT NULL,
    rol_institucional VARCHAR(30) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_finalizacion DATE,
    
    PRIMARY KEY (id_usuario, fecha_inicio),

    CONSTRAINT chk_vinculacion_rol CHECK (rol_institucional IN (
        'Estudiante', 'Profesor', 'Personal Administrativo', 'Egresado'
    )),
    
    CONSTRAINT chk_vinculacion_fechas CHECK (
        fecha_finalizacion IS NULL OR fecha_finalizacion > fecha_inicio
    ),
    
    CONSTRAINT fk_vinculacion_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON DELETE RESTRICT
);


CREATE TABLE estudiante (
    id_usuario BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    promedio_ponderado NUMERIC(4,2) NOT NULL,
    uc_aprobadas INTEGER NOT NULL DEFAULT 0,
    semestre_actual INTEGER NOT NULL,
    facultad_adscripcion VARCHAR(100) NOT NULL,
    escuela_adscripcion VARCHAR(100) NOT NULL,
    
    -- Hereda la Clave Primaria Compuesta
    PRIMARY KEY (id_usuario, fecha_inicio),
    
    -- Restricciones de validación numérica lógica
    CONSTRAINT chk_estudiante_semestre CHECK (semestre_actual >= 1),
    CONSTRAINT chk_estudiante_promedio CHECK (promedio_ponderado BETWEEN 0.00 AND 20.00),
    CONSTRAINT chk_estudiante_uc CHECK (uc_aprobadas >= 0),
    
    -- Relación de Herencia apuntando a la PK compuesta de la madre
    CONSTRAINT fk_estudiante_herencia FOREIGN KEY (id_usuario, fecha_inicio)
        REFERENCES periodo_vinculacion(id_usuario, fecha_inicio) ON DELETE CASCADE
);


CREATE TABLE profesor (
    id_usuario BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    unidad_adscripcion_presupuestaria VARCHAR(150) NOT NULL, -- Ej: 'Facultad de Ingeniería', 'Centro de Investigación (CII)'
    codigo_investigador VARCHAR(50),                         -- Opcional: Solo para profesores investigadores
    cargo_administrativo VARCHAR(100) NOT NULL,

    
    PRIMARY KEY (id_usuario, fecha_inicio),
    
    -- Relación de Herencia
    CONSTRAINT fk_profesor_herencia FOREIGN KEY (id_usuario, fecha_inicio)
        REFERENCES periodo_vinculacion(id_usuario, fecha_inicio) ON DELETE CASCADE
);


CREATE TABLE personal_administrativo (
    id_usuario BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    cargo_administrativo VARCHAR(150) NOT NULL,
    unidad_adscripcion_presupuestaria VARCHAR(150) NOT NULL,
    carga_horaria_semanal INTEGER NOT NULL,
    
    PRIMARY KEY (id_usuario, fecha_inicio),
  
    CONSTRAINT chk_personal_carga_horaria CHECK (carga_horaria_semanal > 0 AND carga_horaria_semanal <= 44),
    
    CONSTRAINT fk_personal_herencia FOREIGN KEY (id_usuario, fecha_inicio)
        REFERENCES periodo_vinculacion(id_usuario, fecha_inicio) ON DELETE CASCADE
);


CREATE TABLE egresado (
    id_usuario BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    titulo_obtenido VARCHAR(150) NOT NULL,
    año_graduacion INTEGER NOT NULL,
    indice_academico_final NUMERIC(4,2) NOT NULL,
    
    PRIMARY KEY (id_usuario, fecha_inicio),
    
    CONSTRAINT chk_egresado_año CHECK (año_graduacion >= 1953), 
    CONSTRAINT chk_egresado_indice CHECK (indice_academico_final BETWEEN 0.00 AND 20.00),
    
    CONSTRAINT fk_egresado_herencia FOREIGN KEY (id_usuario, fecha_inicio)
        REFERENCES periodo_vinculacion(id_usuario, fecha_inicio) ON DELETE CASCADE
);


CREATE TABLE curso_seccion (
    codigo_curso VARCHAR(50) NOT NULL,
    nombre_materia VARCHAR(150) NOT NULL,
    
    PRIMARY KEY (codigo_curso)
);


CREATE TABLE imparte (
    id_profesor BIGINT NOT NULL,
    fecha_inicio_profesor DATE NOT NULL,
    codigo_curso VARCHAR(50) NOT NULL,

    PRIMARY KEY (id_profesor, fecha_inicio_profesor, codigo_curso),
    
    -- Restricciones de Integridad Referencial
    CONSTRAINT fk_imparte_profesor FOREIGN KEY (id_profesor, fecha_inicio_profesor)
        REFERENCES profesor(id_usuario, fecha_inicio) ON DELETE CASCADE,
        
    CONSTRAINT fk_imparte_curso FOREIGN KEY (codigo_curso)
        REFERENCES curso_seccion(codigo_curso) ON DELETE CASCADE
);


CREATE TABLE inscribe (
    id_estudiante BIGINT NOT NULL,
    fecha_inicio_estudiante DATE NOT NULL,
    codigo_curso VARCHAR(50) NOT NULL,
    
    PRIMARY KEY (id_estudiante, fecha_inicio_estudiante, codigo_curso),
    
    CONSTRAINT fk_inscribe_estudiante FOREIGN KEY (id_estudiante, fecha_inicio_estudiante)
        REFERENCES estudiante(id_usuario, fecha_inicio) ON DELETE CASCADE,
        
    CONSTRAINT fk_inscribe_curso FOREIGN KEY (codigo_curso)
        REFERENCES curso_seccion(codigo_curso) ON DELETE CASCADE
);

CREATE TABLE beca (
    id_usuario BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    tipo_beca VARCHAR(50) NOT NULL,
    estatus_beneficio VARCHAR(20) NOT NULL DEFAULT 'Activo',
    cumplimiento_indice BOOLEAN NOT NULL,
    
    PRIMARY KEY (id_usuario, fecha_inicio),
    
    CONSTRAINT chk_beca_tipo CHECK (tipo_beca IN ('Ayuda Económica', 'Excelencia', 'Comedor')),
    CONSTRAINT chk_beca_estatus CHECK (estatus_beneficio IN ('Activo', 'En Evaluación', 'Revocado')),
    
    CONSTRAINT fk_beca_estudiante FOREIGN KEY (id_usuario, fecha_inicio)
        REFERENCES estudiante(id_usuario, fecha_inicio) ON DELETE CASCADE
);

CREATE TABLE preparaduria (
    id_usuario BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    asignatura_asignada VARCHAR(150) NOT NULL,
    horas_ayudantia INTEGER NOT NULL,
    
    PRIMARY KEY (id_usuario, fecha_inicio, asignatura_asignada),
    
    CONSTRAINT chk_preparaduria_horas CHECK (horas_ayudantia > 0),
    
    CONSTRAINT fk_preparaduria_estudiante FOREIGN KEY (id_usuario, fecha_inicio)
        REFERENCES estudiante(id_usuario, fecha_inicio) ON DELETE CASCADE
);

CREATE TABLE beneficiario_familiar (
    ci_familiar BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    parentesco VARCHAR(20) NOT NULL,
    fecha_nacimiento DATE NOT NULL,  --Nuevo atributo
    esquema_vacunacion VARCHAR(20),
    centro_educacion_inicial VARCHAR(20),
    constancia_estudios_universitarios VARCHAR(20),
    certificado_solteria VARCHAR(20),
    
    
    PRIMARY KEY (ci_familiar),
    
    CONSTRAINT chk_familiar_parentesco CHECK (parentesco IN ('Hijo', 'Hija', 'Cónyuge', 'Madre', 'Padre')),
    
    CONSTRAINT chk_familiar_vacunas CHECK (esquema_vacunacion IN ('Entregado', 'Pendiente', 'No Aplica')),
    CONSTRAINT chk_familiar_inicial CHECK (centro_educacion_inicial IN ('Entregado', 'Pendiente', 'No Aplica')),
    CONSTRAINT chk_familiar_universidad CHECK (constancia_estudios_universitarios IN ('Entregado', 'Pendiente', 'No Aplica')),
    CONSTRAINT chk_familiar_solteria CHECK (certificado_solteria IN ('Entregado', 'Pendiente', 'No Aplica')),
    
    CONSTRAINT fk_beneficiario_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON DELETE RESTRICT
);

CREATE TABLE historial_fechas_cobertura (
    id_historial BIGSERIAL, 
    ci_familiar BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE, 
    estatus_lapso VARCHAR(20) NOT NULL DEFAULT 'Activo',
    
    PRIMARY KEY (id_historial),
    
    CONSTRAINT chk_historial_fechas CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio),
    CONSTRAINT chk_historial_estatus CHECK (estatus_lapso IN ('Activo', 'Inhabilitado', 'Vencido')),
    
    CONSTRAINT fk_historial_beneficiario FOREIGN KEY (ci_familiar)
        REFERENCES beneficiario_familiar(ci_familiar) ON DELETE CASCADE
);

CREATE TABLE acompanante_temporal (
    ci_acompanante BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    fecha_expiracion DATE NOT NULL DEFAULT CURRENT_DATE, --Nuevo Atributo
    
    PRIMARY KEY (ci_acompanante, id_usuario),
    
    CONSTRAINT fk_acompanante_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE servicio_publicado (
    id_servicio BIGSERIAL NOT NULL,
    nombre_categoria VARCHAR(100) NOT NULL,
    id_entidad BIGINT NOT NULL,
    nombre_sede VARCHAR(100) NOT NULL,
    
    descripcion_detallada TEXT NOT NULL,
    precio_base_institucional NUMERIC(15,2) NOT NULL,
    ajuste_ubicacion NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    
    PRIMARY KEY (id_servicio),
    
    CONSTRAINT chk_servicio_precio CHECK (precio_base_institucional >= 0),
    
    CONSTRAINT fk_sede FOREIGN KEY (nombre_sede)
	REFERENCES sede (nombre_sede) ON DELETE RESTRICT,
    
    CONSTRAINT fk_servicio_categoria FOREIGN KEY (nombre_categoria)
        REFERENCES categoria_servicio(nombre_categoria) ON DELETE RESTRICT,

    CONSTRAINT fk_servicio_entidad FOREIGN KEY (id_entidad)
        REFERENCES entidad_prestadora(id_entidad) ON DELETE CASCADE
	

);

CREATE TABLE solicitud_servicio (
    id_solicitud BIGSERIAL NOT NULL,
    id_usuario BIGINT NOT NULL,
    id_servicio BIGINT NOT NULL,
    fecha_hora_apertura TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_hora_cierre TIMESTAMP, 
    prioridad_tramite VARCHAR(20) DEFAULT 'Regular', -- Nuevo Atributo
    
    PRIMARY KEY (id_solicitud, id_usuario),
    
    CONSTRAINT chk_solicitud_fechas CHECK (fecha_hora_cierre IS NULL OR fecha_hora_cierre >= fecha_hora_apertura),
    
    CONSTRAINT fk_solicitud_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON DELETE CASCADE,

    CONSTRAINT fk_servicio_publicado FOREIGN KEY (id_servicio) --Nueva foranea
        REFERENCES servicio_publicado (id_servicio) ON DELETE CASCADE
);


--Nueva Tabla 
CREATE TABLE entrega_requisito (
    id_solicitud BIGINT NOT NULL,
    id_acreditacion BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    fecha_entrega DATE NOT NULL DEFAULT CURRENT_DATE,
    estatus_validacion VARCHAR(20) NOT NULL DEFAULT 'En Revisión',
    
    -- La llave primaria compuesta evita que el estudiante suba el mismo papel 2 veces para la misma solicitud
    PRIMARY KEY (id_solicitud, id_acreditacion),
    
    CONSTRAINT fk_entrega_solicitud FOREIGN KEY (id_solicitud, id_usuario) 
        REFERENCES solicitud_servicio(id_solicitud,id_usuario) ON DELETE CASCADE,
        
    CONSTRAINT fk_entrega_acreditacion FOREIGN KEY (id_acreditacion) 
        REFERENCES acreditacion(id_acreditacion) ON DELETE CASCADE,
        
    CONSTRAINT chk_estatus_validacion CHECK (estatus_validacion IN ('En Revisión', 'Aprobado', 'Rechazado'))
);

CREATE TABLE estado_cuenta (
    numero_folio BIGSERIAL NOT NULL,
    id_solicitud BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    
    PRIMARY KEY (numero_folio),

    CONSTRAINT uq_estado_cuenta_solicitud UNIQUE (id_solicitud),
    
    CONSTRAINT fk_estado_cuenta_solicitud FOREIGN KEY (id_solicitud, id_usuario)
        REFERENCES solicitud_servicio(id_solicitud, id_usuario) ON DELETE CASCADE
);

CREATE TABLE paso_actividad (
    id_solicitud BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    secuencia_paso INT NOT NULL,
    estado_paso VARCHAR(50) NOT NULL DEFAULT 'Pendiente',
    oficina_responsable VARCHAR(150) NOT NULL,
    fecha_hora_finalizacion_exacta TIMESTAMP,
    
    PRIMARY KEY (id_solicitud, id_usuario,secuencia_paso),
    
    CONSTRAINT chk_paso_estado CHECK (estado_paso IN ('Pendiente', 'En Proceso', 'Completado')),
    CONSTRAINT chk_paso_secuencia CHECK (secuencia_paso > 0),
    
    CONSTRAINT fk_paso_solicitud FOREIGN KEY (id_solicitud, id_usuario)
        REFERENCES solicitud_servicio(id_solicitud, id_usuario) ON DELETE CASCADE
);

CREATE TABLE item_consumo (
    numero_folio BIGINT NOT NULL,
    numero_linea INT NOT NULL,
    concepto VARCHAR(255) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario NUMERIC(15,2) NOT NULL, 
    impuestos_ley NUMERIC(15,2) NOT NULL,
    
    PRIMARY KEY (numero_folio, numero_linea),
    
    CONSTRAINT chk_item_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_item_precio CHECK (precio_unitario >= 0),
    CONSTRAINT chk_item_impuestos CHECK (impuestos_ley >= 0),
    
    CONSTRAINT fk_item_estado_cuenta FOREIGN KEY (numero_folio)
        REFERENCES estado_cuenta(numero_folio) ON DELETE CASCADE
);

CREATE TABLE acreditacion (
    id_acreditacion BIGSERIAL NOT NULL,
    tipo_acreditacion VARCHAR(100) NOT NULL,
    nombre_acreditacion VARCHAR(150) NOT NULL,
    
    PRIMARY KEY (id_acreditacion),

    CONSTRAINT chk_tipo_acreditacion CHECK (tipo_acreditacion IN('Documento Digital', 'Condición Académica'))
);

CREATE TABLE prestador_interno (
    id_entidad BIGINT NOT NULL,
    codigo_presupuestario VARCHAR(50) NOT NULL,
    director_oficina VARCHAR(150) NOT NULL,
    
    PRIMARY KEY (id_entidad),
    
    CONSTRAINT fk_interno_entidad FOREIGN KEY (id_entidad)
        REFERENCES entidad_prestadora(id_entidad) ON DELETE CASCADE
);

CREATE TABLE aliado_externo (
    id_entidad BIGINT NOT NULL,
    rif VARCHAR(20) NOT NULL,
    razon_social VARCHAR(150) NOT NULL,
    fecha_vencimiento_contrato DATE NOT NULL,
    contactos_legales TEXT NOT NULL,
    
    PRIMARY KEY (id_entidad),
    
    CONSTRAINT uq_aliado_rif UNIQUE (rif),
    
    CONSTRAINT fk_externo_entidad FOREIGN KEY (id_entidad)
        REFERENCES entidad_prestadora(id_entidad) ON DELETE CASCADE
);

CREATE TABLE exige (
    id_servicio BIGINT NOT NULL,
    id_acreditacion BIGINT NOT NULL,
    
    PRIMARY KEY (id_servicio, id_acreditacion),
    
    CONSTRAINT fk_exige_servicio FOREIGN KEY (id_servicio)
        REFERENCES servicio_publicado(id_servicio) ON DELETE CASCADE,
        
    CONSTRAINT fk_exige_acreditacion FOREIGN KEY (id_acreditacion)
        REFERENCES acreditacion(id_acreditacion) ON DELETE CASCADE
);


CREATE TABLE tarifa_diferenciada (
    id_servicio BIGINT NOT NULL,
    perfil_solicitante VARCHAR(50) NOT NULL,
    monto_tarifa NUMERIC(15,2) NOT NULL,
    
    PRIMARY KEY (id_servicio, perfil_solicitante),
    
    CONSTRAINT chk_tarifa_monto CHECK (monto_tarifa >= 0),
    
    CONSTRAINT fk_tarifa_servicio FOREIGN KEY (id_servicio)
        REFERENCES servicio_publicado(id_servicio) ON DELETE CASCADE
);


CREATE TABLE edificacion (
    nombre_sede VARCHAR(100) NOT NULL,
    nombre_edificio VARCHAR(100) NOT NULL,
    direccion_interna VARCHAR(255) NOT NULL,
    
    PRIMARY KEY (nombre_sede, nombre_edificio, direccion_interna),
    
    CONSTRAINT fk_edificacion_sede FOREIGN KEY (nombre_sede)
        REFERENCES sede(nombre_sede) ON DELETE CASCADE
);


CREATE TABLE espacio_fisico (
    nombre_sede VARCHAR(100) NOT NULL,
    nombre_edificio VARCHAR(100) NOT NULL,
    direccion_interna VARCHAR(255) NOT NULL,
    id_espacio VARCHAR(50) NOT NULL, 
    capacidad_maxima_aforo INT NOT NULL,
    tipo_mobiliario VARCHAR(100) NOT NULL,
    estado_mantenimiento VARCHAR(50) NOT NULL DEFAULT 'Operativo',
    registro_disponibilidad VARCHAR(20) NOT NULL DEFAULT 'Disponible',
    
    PRIMARY KEY (nombre_sede, nombre_edificio, id_espacio),
    
    CONSTRAINT chk_espacio_aforo CHECK (capacidad_maxima_aforo > 0),
    CONSTRAINT chk_espacio_estado CHECK (estado_mantenimiento IN ('Operativo', 'En Reparación', 'Clausurado')),
    
    CONSTRAINT chk_espacio_disponibilidad CHECK (registro_disponibilidad IN ('Disponible', 'Ocupado')),
    
    CONSTRAINT fk_espacio_edificacion FOREIGN KEY (nombre_sede, nombre_edificio, direccion_interna)
        REFERENCES edificacion(nombre_sede, nombre_edificio, direccion_interna) ON DELETE CASCADE
);

CREATE TABLE recurso_tecnologico (
    nombre_sede VARCHAR(100) NOT NULL,
    nombre_edificio VARCHAR(100) NOT NULL,
    id_espacio VARCHAR(50) NOT NULL,
    nombre_recurso VARCHAR(50) NOT NULL,
    
    PRIMARY KEY (nombre_sede, nombre_edificio, id_espacio, nombre_recurso),
    
    CONSTRAINT fk_recurso_espacio FOREIGN KEY (nombre_sede, nombre_edificio, id_espacio)
        REFERENCES espacio_fisico(nombre_sede, nombre_edificio, id_espacio) ON DELETE CASCADE
);


CREATE TABLE define_limite (
    nombre_sede VARCHAR(100) NOT NULL,
    nombre_categoria VARCHAR(100) NOT NULL,
    monto_limite_maximo NUMERIC(15,2) NOT NULL,
    PRIMARY KEY (nombre_sede, nombre_categoria),
    
    CONSTRAINT chk_limite_monto CHECK (monto_limite_maximo >= 0),
    
    CONSTRAINT fk_limite_sede FOREIGN KEY (nombre_sede)
        REFERENCES sede(nombre_sede) ON DELETE CASCADE,
        
    CONSTRAINT fk_limite_categoria FOREIGN KEY (nombre_categoria)
        REFERENCES categoria_servicio(nombre_categoria) ON DELETE CASCADE
);

CREATE TABLE reserva (
    id_solicitud BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    nombre_sede VARCHAR(100) NOT NULL,
    nombre_edificio VARCHAR(100) NOT NULL,
    id_espacio VARCHAR(50) NOT NULL,
    fecha_reserva DATE NOT NULL,
    bloque_horario_solicitado VARCHAR(50) NOT NULL,
    
    PRIMARY KEY (id_solicitud, id_usuario, nombre_sede, nombre_edificio, id_espacio, fecha_reserva, bloque_horario_solicitado),
    
    CONSTRAINT fk_reserva_solicitud FOREIGN KEY (id_solicitud, id_usuario)
        REFERENCES solicitud_servicio(id_solicitud, id_usuario) ON DELETE CASCADE,
        
    CONSTRAINT fk_reserva_espacio FOREIGN KEY (nombre_sede, nombre_edificio, id_espacio)
        REFERENCES espacio_fisico(nombre_sede, nombre_edificio, id_espacio) ON DELETE CASCADE
);

CREATE TABLE cargo_adicional (
    id_servicio BIGINT NOT NULL,
    concepto_suplemento VARCHAR(150) NOT NULL,
    monto_cargo NUMERIC(15,2) NOT NULL,
    
    PRIMARY KEY (id_servicio, concepto_suplemento),

    CONSTRAINT chk_cargo_monto CHECK (monto_cargo >= 0),
    
    CONSTRAINT fk_cargo_servicio FOREIGN KEY (id_servicio)
        REFERENCES servicio_publicado(id_servicio) ON DELETE CASCADE
);



CREATE TABLE oportunidad_laboral (
    id_entidad BIGINT NOT NULL,
    codigo_vacante VARCHAR(50) NOT NULL,
    estatus_vacante VARCHAR(50) NOT NULL DEFAULT 'Disponible',
    fecha_oferta DATE NOT NULL,
    responsabilidades TEXT NOT NULL,
    beneficios TEXT NOT NULL,
    perfil_buscado TEXT NOT NULL,
    
    PRIMARY KEY (id_entidad, codigo_vacante),
    
    CONSTRAINT chk_estatus_vacante CHECK (estatus_vacante IN ('Disponible', 'Finalizada')),
    
    CONSTRAINT fk_vacante_aliado FOREIGN KEY (id_entidad)
        REFERENCES aliado_externo(id_entidad) ON DELETE CASCADE
);

CREATE TABLE toma (
    id_usuario BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    id_entidad BIGINT NOT NULL,
    codigo_vacante VARCHAR(50) NOT NULL,
   
    PRIMARY KEY (id_usuario, fecha_inicio, id_entidad, codigo_vacante),
    
    CONSTRAINT fk_toma_egresado FOREIGN KEY (id_usuario, fecha_inicio)
        REFERENCES egresado(id_usuario, fecha_inicio) ON DELETE CASCADE,
        
    CONSTRAINT fk_toma_vacante FOREIGN KEY (id_entidad, codigo_vacante)
        REFERENCES oportunidad_laboral(id_entidad, codigo_vacante) ON DELETE CASCADE
);

CREATE TABLE factura (
    numero_control VARCHAR(50) NOT NULL,
    id_usuario BIGINT NOT NULL,
    numero_folio BIGINT NOT NULL,
    fecha_emision DATE NOT NULL,
    saldo_factura NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    
    PRIMARY KEY (numero_control, id_usuario),
    
    CONSTRAINT chk_factura_saldo CHECK (saldo_factura >= 0),
    
    CONSTRAINT fk_factura_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON DELETE RESTRICT,
        
    CONSTRAINT fk_factura_estado_cuenta FOREIGN KEY (numero_folio)
        REFERENCES estado_cuenta(numero_folio) ON DELETE RESTRICT
);

CREATE TABLE billetera_virtual_tai (
    uid_chip VARCHAR(100) NOT NULL,
    
    id_usuario BIGINT NOT NULL,
    saldo_virtual NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    
    PRIMARY KEY (uid_chip),
    
    CONSTRAINT uq_billetera_usuario UNIQUE (id_usuario),
    
    CONSTRAINT chk_billetera_saldo CHECK (saldo_virtual >= 0),
    
    CONSTRAINT fk_billetera_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE pago_zelle (
    id_pago VARCHAR(50) NOT NULL,
    numero_control_factura VARCHAR(50) NOT NULL,
	id_usuario BIGINT NOT NULL,
    
    fecha_hora_pago TIMESTAMP NOT NULL,
    monto_operacion NUMERIC(15,2) NOT NULL,
    canal_operacion VARCHAR(100) NOT NULL,
    
    correo_origen VARCHAR(150) NOT NULL,
    nombre_titular_emisor VARCHAR(150) NOT NULL,
    codigo_confirmacion VARCHAR(100) NOT NULL,
    
    PRIMARY KEY (id_pago),
    
    CONSTRAINT uq_zelle_confirmacion UNIQUE (codigo_confirmacion),
    CONSTRAINT chk_zelle_monto CHECK (monto_operacion > 0),
    CONSTRAINT chk_canal_operacion CHECK (canal_operacion IN ('Portal Digital', 'Taquilla Presencial')),
    
    CONSTRAINT fk_zelle_factura FOREIGN KEY (numero_control_factura, id_usuario)
        REFERENCES factura(numero_control, id_usuario) ON DELETE CASCADE
);

CREATE TABLE pago_criptomoneda (
    id_pago VARCHAR(50) NOT NULL,
    numero_control_factura VARCHAR(50) NOT NULL,
	id_usuario BIGINT NOT NULL,
    
    fecha_hora_pago TIMESTAMP NOT NULL,
    monto_operacion NUMERIC(15,2) NOT NULL,
    canal_operacion VARCHAR(100) NOT NULL,
    
    hash_transaccion_txid VARCHAR(255) NOT NULL,
    direccion_billetera_origen VARCHAR(255) NOT NULL,
    red_utilizada VARCHAR(50) NOT NULL,
    tasa_conversion_blockchain NUMERIC(20,8) NOT NULL,
    
    PRIMARY KEY (id_pago),
    
    CONSTRAINT uq_cripto_hash UNIQUE (hash_transaccion_txid),
    CONSTRAINT chk_cripto_monto CHECK (monto_operacion > 0),
    CONSTRAINT chk_canal_operacion CHECK (canal_operacion IN ('Portal Digital', 'Taquilla Presencial')),
    CONSTRAINT chk_red_utilizada CHECK (red_utilizada IN ('TRC20', 'ERC20')),
    
    CONSTRAINT fk_cripto_factura FOREIGN KEY (numero_control_factura, id_usuario)
        REFERENCES factura(numero_control, id_usuario) ON DELETE CASCADE
);

CREATE TABLE pago_tarjeta (
    id_pago VARCHAR(50) NOT NULL,
    numero_control_factura VARCHAR(50) NOT NULL,
	id_usuario BIGINT NOT NULL,
    
    fecha_hora_pago TIMESTAMP NOT NULL,
    monto_operacion NUMERIC(15,2) NOT NULL,
    canal_operacion VARCHAR(100) NOT NULL,
    
    numero_tarjeta VARCHAR(20) NOT NULL, -- NO es UNIQUE
    fecha_vencimiento VARCHAR(5) NOT NULL, -- Formato MM/YY
    tipo_red VARCHAR(50) NOT NULL, -- Ej: Visa, MasterCard
    compania_emisora VARCHAR(100) NOT NULL,
    
    PRIMARY KEY (id_pago),
    
    CONSTRAINT chk_tarjeta_monto CHECK (monto_operacion > 0),
    CONSTRAINT chk_canal_operacion CHECK (canal_operacion IN ('Portal Digital', 'Taquilla Presencial')),
    CONSTRAINT chk_tipo_red CHECK (tipo_red IN ('Nacional', 'Internacional')),
    
    CONSTRAINT fk_tarjeta_factura FOREIGN KEY (numero_control_factura, id_usuario)
        REFERENCES factura(numero_control, id_usuario) ON DELETE CASCADE
);

CREATE TABLE pago_movil (
    id_pago VARCHAR(50) NOT NULL,
    numero_control_factura VARCHAR(50) NOT NULL,
	id_usuario BIGINT NOT NULL,
    
    fecha_hora_pago TIMESTAMP NOT NULL,
    monto_operacion NUMERIC(15,2) NOT NULL,
    canal_operacion VARCHAR(100) NOT NULL,
    
    numero_telefono_emisor VARCHAR(20) NOT NULL,
    banco_origen VARCHAR(100) NOT NULL,
    numero_referencia VARCHAR(50) NOT NULL,
    fecha_movimiento DATE NOT NULL,
    
    PRIMARY KEY (id_pago),

    CONSTRAINT uq_movil_referencia UNIQUE (numero_referencia),
    
    CONSTRAINT chk_movil_monto CHECK (monto_operacion > 0),
    CONSTRAINT chk_canal_operacion CHECK (canal_operacion IN ('Portal Digital', 'Taquilla Presencial')),
    
    CONSTRAINT fk_movil_factura FOREIGN KEY (numero_control_factura, id_usuario)
        REFERENCES factura(numero_control, id_usuario) ON DELETE CASCADE
);

CREATE TABLE pago_tai (
    id_pago VARCHAR(50) NOT NULL,
    numero_control_factura VARCHAR(50) NOT NULL,
	id_usuario BIGINT NOT NULL,
    
    fecha_hora_pago TIMESTAMP NOT NULL,
    monto_operacion NUMERIC(15,2) NOT NULL,
    canal_operacion VARCHAR(100) NOT NULL,
    
    uid_chip VARCHAR(100) NOT NULL,
    codigo_terminal_pos VARCHAR(50) NOT NULL,
    
    saldo_remanente NUMERIC(15,2) NOT NULL, 
    
    PRIMARY KEY (id_pago),
    
    CONSTRAINT chk_tai_monto CHECK (monto_operacion > 0),
    CONSTRAINT chk_canal_operacion CHECK (canal_operacion IN ('Portal Digital', 'Taquilla Presencial')),
    
    CONSTRAINT fk_tai_factura FOREIGN KEY (numero_control_factura, id_usuario)
        REFERENCES factura(numero_control, id_usuario) ON DELETE CASCADE,
        
    CONSTRAINT fk_tai_billetera FOREIGN KEY (uid_chip)
        REFERENCES billetera_virtual_tai(uid_chip) ON DELETE RESTRICT
);

CREATE TABLE pago_efectivo (
    id_pago VARCHAR(50) NOT NULL,
    numero_control_factura VARCHAR(50) NOT NULL,
	id_usuario BIGINT NOT NULL,
    
    fecha_hora_pago TIMESTAMP NOT NULL,
    monto_operacion NUMERIC(15,2) NOT NULL,
    canal_operacion VARCHAR(100) NOT NULL,
    
    fecha_hora_vigencia_bcv TIMESTAMP NOT NULL,
    moneda_curso VARCHAR(10) NOT NULL, -- Ej: VED, USD, EUR
    monto_recibido NUMERIC(15,2) NOT NULL,
    
    PRIMARY KEY (id_pago),
    
    CONSTRAINT chk_efectivo_monto CHECK (monto_operacion > 0),
    CONSTRAINT chk_canal_operacion CHECK (canal_operacion IN ('Portal Digital', 'Taquilla Presencial')),
    CONSTRAINT chk_moneda_curso CHECK (moneda_curso IN ('Bolívares', 'Divisas')),
    
    CONSTRAINT fk_efectivo_factura FOREIGN KEY (numero_control_factura, id_usuario)
        REFERENCES factura(numero_control, id_usuario) ON DELETE CASCADE,
        
    CONSTRAINT fk_efectivo_bcv FOREIGN KEY (fecha_hora_vigencia_bcv)
        REFERENCES tasa_cambio_bcv(fecha_hora_vigencia) ON DELETE RESTRICT
);

CREATE TABLE desglose_denominacion_efectivo (
    id_pago VARCHAR(50) NOT NULL,
    
    -- Ej: 'Billete 100 USD', 'Billete 20 USD'
    denominacion_billete VARCHAR(50) NOT NULL,
    cantidad_billetes INT NOT NULL,
    
    PRIMARY KEY (id_pago, denominacion_billete),
    
    CONSTRAINT chk_desglose_cantidad CHECK (cantidad_billetes > 0),
    
    
    CONSTRAINT fk_desglose_efectivo FOREIGN KEY (id_pago)
        REFERENCES pago_efectivo(id_pago) ON DELETE CASCADE
);