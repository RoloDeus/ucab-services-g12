--Trigger de Seguridad Lógica Autónoma
-- Función que ejecuta la lógica de bloqueo
CREATE OR REPLACE FUNCTION fn_seguridad_bloqueo_cuenta()
RETURNS TRIGGER AS $$
BEGIN
    -- Validamos si los intentos fallidos alcanzan o superan el límite de 3
    IF NEW.conteo_intentos_fallidos >= 3 THEN
        NEW.estado_cuenta := 'Bloqueada';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 3. Creamos el Trigger anclado al UPDATE de la columna específica
CREATE TRIGGER trg_seguridad_bloqueo_cuenta
BEFORE UPDATE OF conteo_intentos_fallidos ON usuario
FOR EACH ROW
EXECUTE FUNCTION fn_seguridad_bloqueo_cuenta();


--Procedimiento Almacenado: Registro de Login Exitoso
CREATE OR REPLACE PROCEDURE sp_registrar_login_exitoso(
    p_cedula VARCHAR
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Se actualiza la última conexión y se resetean los errores a 0
    UPDATE usuario
    SET ultima_conexion = CURRENT_TIMESTAMP,
        conteo_intentos_fallidos = 0
    WHERE cedula = p_cedula 
      AND estado_cuenta = 'Activa'; -- Solo si la cuenta no fue bloqueada previamente
END;
$$;


--Cálculo del Índice de Recurrencia
CREATE OR REPLACE PROCEDURE sp_actualizar_fidelidad(
    p_id_usuario BIGINT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_total_tramites INT;
BEGIN
    -- Contamos cuántas solicitudes de servicio ha realizado el usuario histórico
    SELECT COUNT(*) INTO v_total_tramites
    FROM solicitud_servicio
    WHERE id_usuario = p_id_usuario;
    
    -- Actualizamos su perfil basándonos en reglas de negocio (CORREGIDO 'VIP' por 'Preferencial')
    UPDATE usuario
    SET indice_recurrencia = v_total_tramites,
        categoria_fidelidad = CASE 
            WHEN v_total_tramites >= 15 THEN 'Preferencial'
            WHEN v_total_tramites >= 5 THEN 'Frecuente'
            ELSE 'Regular'
        END
    WHERE id_usuario = p_id_usuario;
END;
$$;

--Solictud de Beca: Procedimiento Almacenado
CREATE OR REPLACE PROCEDURE sp_solicitar_beca(
    p_cedula VARCHAR,
    p_tipo_beca VARCHAR
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_id_usuario BIGINT;
    v_fecha_inicio DATE;
    v_promedio NUMERIC(4,2);
BEGIN
    -- 1. Buscamos el ID, el promedio y, MUY IMPORTANTE, la fecha_inicio del estudiante
    SELECT u.id_usuario, e.fecha_inicio, e.promedio_ponderado
    INTO v_id_usuario, v_fecha_inicio, v_promedio
    FROM usuario u
    JOIN estudiante e ON u.id_usuario = e.id_usuario
    WHERE u.cedula = p_cedula AND u.estado_cuenta = 'Activa';

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Estudiante no encontrado o cuenta inactiva.';
    END IF;

    -- 2. Motor de Reglas de Negocio (Promedios)
    IF p_tipo_beca = 'Excelencia' AND v_promedio < 16.00 THEN
        RAISE EXCEPTION 'Solicitud rechazada. La Beca de Excelencia exige un promedio > 16.00. Promedio actual: %', v_promedio;
    ELSIF p_tipo_beca = 'Ayuda Económica' AND v_promedio < 14.00 THEN
        RAISE EXCEPTION 'Solicitud rechazada. La Ayuda Económica exige un promedio > 14.00. Promedio actual: %', v_promedio;
    END IF;

    -- 3. Inserción perfecta: Usamos la v_fecha_inicio para que haga match exacto con la Llave Foránea
    INSERT INTO beca (id_usuario, fecha_inicio, tipo_beca, estatus_beneficio, cumplimiento_indice)
    VALUES (v_id_usuario, v_fecha_inicio, p_tipo_beca, 'Activo', TRUE);

END;
$$;

--Trigger de Auditoría de Mantenimiento de Beca
CREATE OR REPLACE FUNCTION fn_auditar_mantenimiento_beca()
RETURNS TRIGGER AS $$
BEGIN
    -- Si el promedio bajó de 16.00, buscamos si tiene Beca de Excelencia y la ponemos "En Evaluación"
    IF NEW.promedio_ponderado < 16.00 THEN
        UPDATE beca
        SET cumplimiento_indice = FALSE,
            estatus_beneficio = 'En Evaluación'
        WHERE id_usuario = NEW.id_usuario 
          AND tipo_beca = 'Excelencia' 
          AND estatus_beneficio = 'Activo';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_auditar_mantenimiento_beca
AFTER UPDATE OF promedio_ponderado ON estudiante
FOR EACH ROW
EXECUTE FUNCTION fn_auditar_mantenimiento_beca();



--Trigger de Ruptura de Vínculo Familiar
-- Función que ejecuta la lógica de inhabilitación de cobertura
CREATE OR REPLACE FUNCTION fn_ruptura_vinculo_beneficiarios()
RETURNS TRIGGER AS $$
BEGIN
    -- Si la cuenta del titular pasa a estar inactiva (Suspendida o Bloqueada)
    IF NEW.estado_cuenta IN ('Suspendida', 'Bloqueada') AND OLD.estado_cuenta = 'Activa' THEN
        
        -- Buscamos a todos sus familiares y cerramos su lapso de cobertura activo
        -- CORRECCIÓN: Nombre correcto de la tabla
        UPDATE historial_fechas_cobertura 
        SET fecha_fin = CURRENT_DATE, 
            estatus_lapso = 'Inhabilitado'
        WHERE ci_familiar IN (
            SELECT ci_familiar 
            FROM beneficiario_familiar 
            WHERE id_usuario = NEW.id_usuario -- CORRECCIÓN: Nombre de columna correcto
        ) 
        AND estatus_lapso = 'Activo';
        
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_ruptura_vinculo_beneficiarios
AFTER UPDATE OF estado_cuenta ON usuario
FOR EACH ROW
EXECUTE FUNCTION fn_ruptura_vinculo_beneficiarios();



--Procedimiento Almacenado: Transición por Mayoría de Edad
CREATE OR REPLACE PROCEDURE sp_transicion_mayoria_edad()
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE beneficiario_familiar
    SET constancia_estudios_universitarios = 'Pendiente',
        certificado_solteria = 'Pendiente',
	centro_educacion_inicial = 'Pendiente'
    WHERE LOWER(TRIM(parentesco)) IN ('hijo', 'hija', 'hijo/a') -- Valida el parentesco
      AND EXTRACT(YEAR FROM AGE(CURRENT_DATE, fecha_nacimiento)) >= 18 -- Valida que sea mayor de edad
      AND (
          LOWER(TRIM(constancia_estudios_universitarios)) = 'no aplica' 
          OR LOWER(TRIM(certificado_solteria)) = 'no aplica'
	  OR LOWER(TRIM(centro_educacion_inicial)) = 'no aplica'
      ); 
END;
$$;



--Procedimiento Batch: Limpieza de Acompañantes Temporales
CREATE OR REPLACE PROCEDURE sp_archivar_acompanantes_temporales()
LANGUAGE plpgsql
AS $$
BEGIN
    -- Borramos a todos los acompañantes cuyo pase temporal sea menor a la fecha de hoy
    DELETE FROM acompanante_temporal
    WHERE fecha_expiracion < CURRENT_DATE;
END;
$$;


--Trigger: El "Candado de Disponibilidad" (Prevención de Solapamiento)
-- Función que ejecuta la validación del candado
CREATE OR REPLACE FUNCTION fn_candado_disponibilidad_reserva()
RETURNS TRIGGER AS $$
DECLARE
    v_existe INT;
BEGIN
    -- Buscamos si ya hay una reserva para ese mismo lugar, día y hora exacta
    SELECT COUNT(*) INTO v_existe
    FROM reserva
    WHERE nombre_sede = NEW.nombre_sede
      AND nombre_edificio = NEW.nombre_edificio
      AND id_espacio = NEW.id_espacio
      AND fecha_reserva = NEW.fecha_reserva
      AND bloque_horario_solicitado = NEW.bloque_horario_solicitado;

    -- Si el conteo es mayor a 0, lanzamos el error para bloquear el INSERT
    IF v_existe > 0 THEN
        RAISE EXCEPTION 'Candado de Seguridad: El espacio % en el edificio % ya está reservado para la fecha % en el bloque %.', 
        NEW.id_espacio, NEW.nombre_edificio, NEW.fecha_reserva, NEW.bloque_horario_solicitado;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger que se dispara ANTES de insertar una nueva reserva
CREATE TRIGGER trg_candado_disponibilidad_reserva
BEFORE INSERT OR UPDATE ON reserva
FOR EACH ROW
EXECUTE FUNCTION fn_candado_disponibilidad_reserva();



--Trigger: Validación del Estado de Mantenimiento
CREATE OR REPLACE FUNCTION fn_validar_mantenimiento_espacio()
RETURNS TRIGGER AS $$
DECLARE
    v_estado_mantenimiento VARCHAR(50);
BEGIN
    -- Vamos a la tabla de infraestructura a ver cómo está el espacio físico
    SELECT estado_mantenimiento INTO v_estado_mantenimiento
    FROM espacio_fisico
    WHERE nombre_sede = NEW.nombre_sede
      AND nombre_edificio = NEW.nombre_edificio
      AND id_espacio = NEW.id_espacio;

    -- Si está dañado o en mantenimiento, bloqueamos la reserva de inmediato
    IF v_estado_mantenimiento IN ('En Reparación', 'Clausurado') THEN
        RAISE EXCEPTION 'Operación rechazada: El espacio % no se puede reservar porque se encuentra en estado de %.', 
        NEW.id_espacio, v_estado_mantenimiento;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validar_mantenimiento_espacio
BEFORE INSERT ON reserva
FOR EACH ROW
EXECUTE FUNCTION fn_validar_mantenimiento_espacio();


--Procedure: Actualizador de la "Caché de Disponibilidad"
CREATE OR REPLACE PROCEDURE sp_actualizar_cache_disponibilidad()
LANGUAGE plpgsql
AS $$
BEGIN
    -- 1. Ponemos Fuera de Servicio todo lo que esté dañado o en mantenimiento
    UPDATE espacio_fisico
    SET registro_disponibilidad = 'Ocupado'
    WHERE estado_mantenimiento IN ('En Reparación', 'Clausurado');

    -- 2. Ponemos Disponible todo lo que esté Operativo
    UPDATE espacio_fisico
    SET registro_disponibilidad = 'Disponible'
    WHERE estado_mantenimiento = 'Operativo';

    -- (Nota: El estado 'Ocupado' lo controlarán las reservas por bloque horario)
END;
$$;


--Trigger de Integridad Transaccional: Billetera TAI
-- Función que procesa el débito y calcula el atributo derivado
CREATE OR REPLACE FUNCTION fn_procesar_pago_tai()
RETURNS TRIGGER AS $$
DECLARE
    v_saldo_actual NUMERIC;
BEGIN
    -- 1. Buscamos el saldo que tiene el usuario en su chip
    SELECT saldo_virtual INTO v_saldo_actual
    FROM billetera_virtual_tai
    WHERE uid_chip = NEW.uid_chip;

    -- 2. Verificamos si tiene fondos suficientes
    IF v_saldo_actual < NEW.monto_operacion THEN
        RAISE EXCEPTION 'Fondos Insuficientes en la Billetera TAI. Saldo actual: %', v_saldo_actual;
    END IF;

    -- 3. Le descontamos el dinero de la billetera
    UPDATE billetera_virtual_tai
    SET saldo_virtual = saldo_virtual - NEW.monto_operacion
    WHERE uid_chip = NEW.uid_chip;

    -- 4. Registramos en el recibo (la tabla pago_tai) cuánto le quedó después de pagar
    NEW.saldo_remanente := v_saldo_actual - NEW.monto_operacion;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger que se dispara ANTES de registrar el pago
CREATE TRIGGER trg_procesar_pago_tai
BEFORE INSERT ON pago_tai
FOR EACH ROW
EXECUTE FUNCTION fn_procesar_pago_tai();



--Trigger Polimórfico: Amortización de Facturas
CREATE OR REPLACE FUNCTION fn_amortizar_saldo_factura()
RETURNS TRIGGER AS $$
BEGIN
    -- Descontamos el monto de la operación del saldo de la factura
    -- USANDO LA LLAVE COMPUESTA EXACTA (numero y usuario)
    UPDATE factura
    SET saldo_factura = saldo_factura - NEW.monto_operacion
    WHERE numero_control = NEW.numero_control_factura
      AND id_usuario = NEW.id_usuario;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Conectamos esta MISMA función a múltiples tablas de pago (Table-per-Concrete-Class)
CREATE TRIGGER trg_amortizar_factura_zelle
AFTER INSERT ON pago_zelle FOR EACH ROW EXECUTE FUNCTION fn_amortizar_saldo_factura();

CREATE TRIGGER trg_amortizar_factura_tarjeta
AFTER INSERT ON pago_tarjeta FOR EACH ROW EXECUTE FUNCTION fn_amortizar_saldo_factura();

CREATE TRIGGER trg_amortizar_factura_tai
AFTER INSERT ON pago_tai FOR EACH ROW EXECUTE FUNCTION fn_amortizar_saldo_factura();




--Procedimiento Almacenado: Cierre de Trámite y Generación de Factura
CREATE OR REPLACE PROCEDURE sp_generar_factura_desde_estado_cuenta(
    p_numero_folio BIGINT, 
    p_ci_usuario VARCHAR
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_id_usuario BIGINT;
    v_total_factura NUMERIC(15,2);
    v_nuevo_numero_control VARCHAR(50);
BEGIN
    -- 1. Traducimos la Cédula (VARCHAR) al ID de Usuario (BIGINT)
    SELECT id_usuario INTO v_id_usuario
    FROM usuario
    WHERE cedula = p_ci_usuario;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'No existe un usuario con la cédula %', p_ci_usuario;
    END IF;

    -- 2. Calculamos la suma total de los consumos del estado de cuenta
    -- Fórmula: (cantidad * precio_unitario) + impuestos
    SELECT COALESCE(SUM((cantidad * precio_unitario) + impuestos_ley), 0)
    INTO v_total_factura
    FROM item_consumo
    WHERE numero_folio = p_numero_folio;

    -- 3. Generamos un código de control único (Ej: FAC-FOLIO-1000)
    v_nuevo_numero_control := 'FAC-FOLIO-' || p_numero_folio;

    -- 4. Creamos la factura
    INSERT INTO factura (numero_control, id_usuario, numero_folio, fecha_emision, saldo_factura)
    VALUES (v_nuevo_numero_control, v_id_usuario, p_numero_folio, CURRENT_DATE, v_total_factura);
END;
$$;


--Trigger: Validador de Consignación de Requisitos
-- Función que valida si el usuario cumple la malla de requisitos
CREATE OR REPLACE FUNCTION fn_validar_requisitos_tramite()
RETURNS TRIGGER AS $$
DECLARE
    v_requisitos_faltantes INT;
BEGIN
    -- Validamos si la solicitud nace cerrada o si alguien intenta actualizarla para cerrarla
    IF NEW.fecha_hora_cierre IS NOT NULL THEN
        
        -- Buscamos EXACTAMENTE cuántos requisitos pide el servicio
        -- y verificamos que NO EXISTAN en la tabla de entregas del estudiante
        SELECT COUNT(e.id_acreditacion) INTO v_requisitos_faltantes
        FROM exige e
        WHERE e.id_servicio = NEW.id_servicio 
          AND NOT EXISTS (
              SELECT 1 
              FROM entrega_requisito er 
              WHERE er.id_solicitud = NEW.id_solicitud 
                AND er.id_acreditacion = e.id_acreditacion
          );

        -- Si la cuenta de faltantes da más de 0, trancamos la operación inmediatamente
        IF v_requisitos_faltantes > 0 THEN
            RAISE EXCEPTION ' Trámite bloqueado. Faltan % requisito(s) por consignar para procesar la solicitud %.', v_requisitos_faltantes, NEW.id_solicitud;
        END IF;
        
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validar_requisitos_tramite
BEFORE INSERT OR UPDATE ON solicitud_servicio
FOR EACH ROW EXECUTE FUNCTION fn_validar_requisitos_tramite();



--Procedimiento: Auditoría de Vencimiento de Documentos
CREATE OR REPLACE PROCEDURE sp_auditar_vencimiento_documentos()
LANGUAGE plpgsql
AS $$
BEGIN
    -- 1. Auditar y eliminar aliados externos cuyo contrato expiró
    -- (Opcionalmente podría cambiar un estatus, pero según Creates.txt no hay estatus en aliado_externo, 
    -- por ende, se revoca el permiso o se elimina según la regla).
    DELETE FROM aliado_externo
    WHERE fecha_vencimiento_contrato < CURRENT_DATE;

    -- 2. Auditar fechas de cobertura familiar (basado en historial_fechas_cobertura)
    UPDATE historial_fechas_cobertura
    SET estatus_lapso = 'Vencido'
    WHERE fecha_fin < CURRENT_DATE AND estatus_lapso = 'Activo';
END;
$$;


--Función: Cálculo de Prioridad de Atención
CREATE OR REPLACE FUNCTION fn_calcular_prioridad_tramite(p_id_usuario BIGINT)
RETURNS VARCHAR AS $$
DECLARE
    v_fidelidad VARCHAR(20);
    v_prioridad VARCHAR(20);
BEGIN
    -- Obtenemos directamente la categoría calculada del usuario
    SELECT categoria_fidelidad INTO v_fidelidad
    FROM usuario
    WHERE id_usuario = p_id_usuario;
    
    -- Mapeo de Fidelidad a Prioridad del Trámite
    v_prioridad := CASE 
        WHEN v_fidelidad = 'Preferencial' THEN 'Alta'
        WHEN v_fidelidad = 'Frecuente' THEN 'Media'
        ELSE 'Baja'
    END;
    
    RETURN v_prioridad;
END;
$$ LANGUAGE plpgsql;


--Trigger de Trazabilidad y Cierre Automático
CREATE OR REPLACE FUNCTION fn_avance_secuencial_paso() 
RETURNS TRIGGER AS $$ 
DECLARE 
    v_total_pasos INT; 
    v_pasos_completados INT;
BEGIN 
    -- 1. Inyectar hora exacta si se completó el paso
    IF NEW.estado_paso = 'Completado' AND OLD.estado_paso != 'Completado' THEN
        NEW.fecha_hora_finalizacion_exacta := CURRENT_TIMESTAMP;
    END IF;

    -- 2. Trazabilidad de cierre (Validar si es el último paso)
    -- Contar pasos totales para ESTA solicitud y ESTE usuario
    SELECT COUNT(*) INTO v_total_pasos 
    FROM paso_actividad 
    WHERE id_solicitud = NEW.id_solicitud AND id_usuario = NEW.id_usuario;

    -- Contar cuántos están en 'Completado'
    SELECT COUNT(*) INTO v_pasos_completados 
    FROM paso_actividad 
    WHERE id_solicitud = NEW.id_solicitud AND id_usuario = NEW.id_usuario AND estado_paso = 'Completado';

    -- Si los completados + este nuevo paso alcanzan el total, cerramos el trámite matriz
    -- CORRECCIÓN APLICADA: Uso de llave compuesta en el UPDATE
    IF v_pasos_completados >= v_total_pasos THEN
        UPDATE solicitud_servicio
        SET fecha_hora_cierre = CURRENT_TIMESTAMP
        WHERE id_solicitud = NEW.id_solicitud 
          AND id_usuario = NEW.id_usuario; -- Llave compuesta obligatoria
    END IF;

    RETURN NEW;
END; 
$$ LANGUAGE plpgsql;

-- El trigger debe estar anclado correctamente
CREATE TRIGGER trg_avance_secuencial
BEFORE UPDATE OF estado_paso ON paso_actividad
FOR EACH ROW
EXECUTE FUNCTION fn_avance_secuencial_paso();


-- 2. Trigger Posterior: Cierre de la Solicitud
CREATE OR REPLACE FUNCTION fn_cerrar_solicitud_automatica()
RETURNS TRIGGER AS $$
DECLARE
    v_pasos_pendientes INT;
BEGIN
    -- Validación de estado del paso actual
    IF NEW.estado_paso = 'Completado' THEN
        -- Conteo unificado de pasos no completados para la solicitud específica
        SELECT COUNT(*) INTO v_pasos_pendientes
        FROM paso_actividad
        WHERE id_solicitud = NEW.id_solicitud 
          AND id_usuario = NEW.id_usuario 
          AND estado_paso != 'Completado';

        -- Cierre automático utilizando llave primaria compuesta
        IF v_pasos_pendientes = 0 THEN
            UPDATE solicitud_servicio
            SET fecha_hora_cierre = CURRENT_TIMESTAMP
            WHERE id_solicitud = NEW.id_solicitud 
              AND id_usuario = NEW.id_usuario;
        END IF;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Anclaje del trigger
CREATE TRIGGER trg_cerrar_solicitud
AFTER UPDATE OF estado_paso ON paso_actividad
FOR EACH ROW
EXECUTE FUNCTION fn_cerrar_solicitud_automatica();



--Trigger del "Pago Único al 100%" y Tasa BCV
CREATE OR REPLACE FUNCTION fn_validar_y_procesar_pago()
RETURNS TRIGGER AS $$
DECLARE
    v_saldo_pendiente NUMERIC(15,2);
    v_monto_calculado NUMERIC(15,2);
BEGIN
    -- Extracción del saldo pendiente usando llave compuesta
    SELECT saldo_factura INTO v_saldo_pendiente
    FROM factura
    WHERE numero_control = NEW.numero_control_factura
      AND id_usuario = NEW.id_usuario;

    v_monto_calculado := NEW.monto_operacion; 

    -- Restricción de abonos parciales (Monto exacto requerido)
    IF v_monto_calculado < (v_saldo_pendiente - 0.01) THEN
        RAISE EXCEPTION 'Pago rechazado. No se permiten abonos parciales. Monto requerido: %', v_saldo_pendiente;
    END IF;

    -- Liquidación de factura (Solo actualizamos el saldo, porque tu tabla no tiene estado_factura)
    UPDATE factura
    SET saldo_factura = 0
    WHERE numero_control = NEW.numero_control_factura
      AND id_usuario = NEW.id_usuario;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validar_pago_efectivo
BEFORE INSERT ON pago_efectivo
FOR EACH ROW
EXECUTE FUNCTION fn_validar_y_procesar_pago();

CREATE TRIGGER trg_validar_pago_zelle
BEFORE INSERT ON pago_zelle  
FOR EACH ROW
EXECUTE FUNCTION fn_validar_y_procesar_pago();

CREATE TRIGGER trg_validar_pago_tarjeta
BEFORE INSERT ON pago_tarjeta 
FOR EACH ROW
EXECUTE FUNCTION fn_validar_y_procesar_pago();

CREATE TRIGGER trg_validar_pago_criptomoneda
BEFORE INSERT ON pago_criptomoneda
FOR EACH ROW
EXECUTE FUNCTION fn_validar_y_procesar_pago();

CREATE TRIGGER trg_validar_pago_movil
BEFORE INSERT ON pago_movil
FOR EACH ROW
EXECUTE FUNCTION fn_validar_y_procesar_pago();

CREATE TRIGGER trg_validar_pago_tai
BEFORE INSERT ON pago_tai 
FOR EACH ROW
EXECUTE FUNCTION fn_validar_y_procesar_pago();

------------------------------------- Triggers Funciones (paola)---------------------------------------------
-- ============================================================
-- HU-66 — Bitácora Inmutable de Tiempos
-- ============================================================

-- 1) Función para grabar la hora automáticamente al completar un paso
CREATE OR REPLACE FUNCTION fn_grabar_hora_finalizacion()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.estado_paso = 'Completado' AND NEW.fecha_hora_finalizacion_exacta IS NULL THEN
        NEW.fecha_hora_finalizacion_exacta := CURRENT_TIMESTAMP;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger asociado a fn_grabar_hora_finalizacion
DROP TRIGGER IF EXISTS trg_grabar_hora ON paso_actividad;
CREATE TRIGGER trg_grabar_hora
    BEFORE INSERT OR UPDATE ON paso_actividad
    FOR EACH ROW
    EXECUTE FUNCTION fn_grabar_hora_finalizacion();

-- 2) Función para prohibir la edición de la hora de finalización
CREATE OR REPLACE FUNCTION fn_bloquear_edicion_hora()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.fecha_hora_finalizacion_exacta IS NOT NULL
       AND NEW.fecha_hora_finalizacion_exacta IS DISTINCT FROM OLD.fecha_hora_finalizacion_exacta THEN
        RAISE EXCEPTION 'La hora de finalización es inmutable y no puede modificarse (paso % de solicitud %).',
            OLD.secuencia_paso, OLD.id_solicitud;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger asociado a fn_bloquear_edicion_hora
DROP TRIGGER IF EXISTS trg_bloquear_hora ON paso_actividad;
CREATE TRIGGER trg_bloquear_hora
    BEFORE UPDATE ON paso_actividad
    FOR EACH ROW
    EXECUTE FUNCTION fn_bloquear_edicion_hora();


-- ============================================================
-- HU-65 — Bloqueo de Espacios (anti-solapamiento)
-- ============================================================

-- Función para rechazar reservas solapadas en fecha y bloque horario
CREATE OR REPLACE FUNCTION fn_bloquear_reserva_solapada()
RETURNS TRIGGER AS $$
DECLARE
    conflictos INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO conflictos
    FROM reserva r
    WHERE r.nombre_sede     = NEW.nombre_sede
      AND r.nombre_edificio = NEW.nombre_edificio
      AND r.id_espacio      = NEW.id_espacio
      AND r.fecha_reserva   = NEW.fecha_reserva
      AND r.bloque_horario_solicitado = NEW.bloque_horario_solicitado
      AND NOT (r.id_solicitud = NEW.id_solicitud AND r.id_usuario = NEW.id_usuario);

    IF conflictos > 0 THEN
        RAISE EXCEPTION 'El espacio % ya está reservado el % en el bloque %.',
            NEW.id_espacio, NEW.fecha_reserva, NEW.bloque_horario_solicitado;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger asociado a fn_bloquear_reserva_solapada
DROP TRIGGER IF EXISTS trg_bloquear_reserva ON reserva;
CREATE TRIGGER trg_bloquear_reserva
    BEFORE INSERT OR UPDATE ON reserva
    FOR EACH ROW
    EXECUTE FUNCTION fn_bloquear_reserva_solapada();


-- ============================================================
-- HU-61 — Validación de Límites de Tarifas
-- ============================================================

-- Función para impedir publicar servicios que superen el tope de su categoría por sede
CREATE OR REPLACE FUNCTION fn_validar_limite_tarifa()
RETURNS TRIGGER AS $$
DECLARE
    precio_total NUMERIC(15,2);
    tope_sede    NUMERIC(15,2);
BEGIN
    precio_total := NEW.precio_base_institucional + NEW.ajuste_ubicacion;

    IF NEW.nombre_sede IS NULL THEN
        RAISE EXCEPTION 'El servicio no tiene sede asignada; no se puede validar su tope.';
    END IF;

    SELECT dl.monto_limite_maximo
    INTO tope_sede
    FROM define_limite dl
    WHERE dl.nombre_categoria = NEW.nombre_categoria
      AND dl.nombre_sede      = NEW.nombre_sede;

    IF tope_sede IS NULL THEN
        RAISE EXCEPTION 'No hay tope definido para la categoría "%" en la sede "%".',
            NEW.nombre_categoria, NEW.nombre_sede;
    END IF;

    IF precio_total > tope_sede THEN
        RAISE EXCEPTION 'El precio % supera el tope de % para la categoría "%" en la sede "%".',
            precio_total, tope_sede, NEW.nombre_categoria, NEW.nombre_sede;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger asociado a fn_validar_limite_tarifa
DROP TRIGGER IF EXISTS trg_validar_tarifa ON servicio_publicado;
CREATE TRIGGER trg_validar_tarifa
    BEFORE INSERT OR UPDATE ON servicio_publicado
    FOR EACH ROW
    EXECUTE FUNCTION fn_validar_limite_tarifa();


-- ============================================================
-- HU-62 — Liquidación Automática de Facturas
-- ============================================================

-- Función para descontar del saldo con cada pago y bloquear facturas liquidadas
CREATE OR REPLACE FUNCTION fn_liquidar_factura()
RETURNS TRIGGER AS $$
DECLARE
    saldo_actual NUMERIC(15,2);
BEGIN
    SELECT saldo_factura
    INTO saldo_actual
    FROM factura
    WHERE numero_control = NEW.numero_control_factura
      AND id_usuario     = NEW.id_usuario;

    IF saldo_actual <= 0 THEN
        RAISE EXCEPTION 'La factura % ya está liquidada; no acepta más pagos.',
            NEW.numero_control_factura;
    END IF;

    IF NEW.monto_operacion > saldo_actual THEN
        RAISE EXCEPTION 'El pago de % excede el saldo pendiente de la factura % (saldo: %).',
            NEW.monto_operacion, NEW.numero_control_factura, saldo_actual;
    END IF;

    UPDATE factura
    SET saldo_factura = saldo_factura - NEW.monto_operacion
    WHERE numero_control = NEW.numero_control_factura
      AND id_usuario     = NEW.id_usuario;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers asociados a fn_liquidar_factura (se ejecutan tras registrar pagos en las 4 pasarelas)
DROP TRIGGER IF EXISTS trg_liquidar_zelle    ON pago_zelle;
CREATE TRIGGER trg_liquidar_zelle    AFTER INSERT ON pago_zelle    FOR EACH ROW EXECUTE FUNCTION fn_liquidar_factura();

DROP TRIGGER IF EXISTS trg_liquidar_tarjeta  ON pago_tarjeta;
CREATE TRIGGER trg_liquidar_tarjeta  AFTER INSERT ON pago_tarjeta  FOR EACH ROW EXECUTE FUNCTION fn_liquidar_factura();

DROP TRIGGER IF EXISTS trg_liquidar_movil    ON pago_movil;
CREATE TRIGGER trg_liquidar_movil    AFTER INSERT ON pago_movil    FOR EACH ROW EXECUTE FUNCTION fn_liquidar_factura();

DROP TRIGGER IF EXISTS trg_liquidar_efectivo ON pago_efectivo;
CREATE TRIGGER trg_liquidar_efectivo AFTER INSERT ON pago_efectivo FOR EACH ROW EXECUTE FUNCTION fn_liquidar_factura();


-- ============================================================
-- HU-63 — Suspensión por Desvinculación
-- ============================================================

-- Función para suspender cuentas de usuario sin vínculos activos
CREATE OR REPLACE FUNCTION fn_suspender_por_desvinculacion()
RETURNS TRIGGER AS $$
DECLARE
    vinculos_activos INTEGER;
BEGIN
    SELECT COUNT(*)
    INTO vinculos_activos
    FROM periodo_vinculacion
    WHERE id_usuario = NEW.id_usuario
      AND (fecha_finalizacion IS NULL OR fecha_finalizacion > CURRENT_DATE);

    IF vinculos_activos = 0 THEN
        UPDATE usuario
        SET estado_cuenta = 'Suspendida'
        WHERE id_usuario = NEW.id_usuario
          AND estado_cuenta <> 'Suspendida';

        RAISE NOTICE 'Usuario % suspendido: no le quedan vínculos activos.', NEW.id_usuario;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger asociado a fn_suspender_por_desvinculacion
DROP TRIGGER IF EXISTS trg_suspender_desvinculacion ON periodo_vinculacion;
CREATE TRIGGER trg_suspender_desvinculacion
    AFTER INSERT OR UPDATE ON periodo_vinculacion
    FOR EACH ROW
    EXECUTE FUNCTION fn_suspender_por_desvinculacion();

-- ============================================================
-- HU-60 — Control de Acceso por Rol (RBAC / RLS)
-- ============================================================
CREATE ROLE rol_profesor NOLOGIN;

GRANT SELECT ON estudiante    TO rol_profesor;
GRANT SELECT ON inscribe      TO rol_profesor;
GRANT SELECT ON imparte       TO rol_profesor;
GRANT SELECT ON curso_seccion TO rol_profesor;
GRANT SELECT ON usuario       TO rol_profesor;

ALTER TABLE estudiante ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS pol_profesor_sus_alumnos ON estudiante;
CREATE POLICY pol_profesor_sus_alumnos ON estudiante
    FOR SELECT
    TO rol_profesor
    USING (
        EXISTS (
            SELECT 1
            FROM inscribe i
            JOIN imparte im     ON im.codigo_curso = i.codigo_curso
            JOIN usuario u_prof ON u_prof.id_usuario = im.id_profesor
            WHERE i.id_estudiante = estudiante.id_usuario
              AND u_prof.correo_institucional = current_user
        )
    );


-- ============================================================
-- HU-67 — Cálculo de Tiempos (devuelven días ENTEROS)
-- ============================================================

-- Función para calcular la duración total de un trámite
DROP FUNCTION IF EXISTS fn_duracion_tramite(BIGINT, BIGINT);
CREATE FUNCTION fn_duracion_tramite(p_id_solicitud BIGINT, p_id_usuario BIGINT)
RETURNS INTEGER AS $$
DECLARE
    v_apertura TIMESTAMP;
    v_cierre   TIMESTAMP;
BEGIN
    SELECT fecha_hora_apertura, fecha_hora_cierre
    INTO v_apertura, v_cierre
    FROM solicitud_servicio
    WHERE id_solicitud = p_id_solicitud AND id_usuario = p_id_usuario;

    IF v_apertura IS NULL THEN
        RETURN NULL;
    END IF;

    IF v_cierre IS NULL THEN
        v_cierre := CURRENT_TIMESTAMP;   -- trámite abierto: mide hasta hoy
    END IF;

    RETURN ROUND(EXTRACT(EPOCH FROM (v_cierre - v_apertura)) / 86400.0);
END;
$$ LANGUAGE plpgsql;

-- Función para calcular el tiempo transcurrido entre un paso y su anterior
DROP FUNCTION IF EXISTS fn_duracion_paso(BIGINT, BIGINT, INT);
CREATE FUNCTION fn_duracion_paso(p_id_solicitud BIGINT, p_id_usuario BIGINT, p_secuencia INT)
RETURNS INTEGER AS $$
DECLARE
    v_fin_actual   TIMESTAMP;
    v_fin_anterior TIMESTAMP;
BEGIN
    SELECT fecha_hora_finalizacion_exacta INTO v_fin_actual
    FROM paso_actividad
    WHERE id_solicitud = p_id_solicitud AND id_usuario = p_id_usuario AND secuencia_paso = p_secuencia;

    SELECT fecha_hora_finalizacion_exacta INTO v_fin_anterior
    FROM paso_actividad
    WHERE id_solicitud = p_id_solicitud AND id_usuario = p_id_usuario AND secuencia_paso = p_secuencia - 1;

    IF v_fin_actual IS NULL OR v_fin_anterior IS NULL THEN
        RETURN NULL;
    END IF;

    RETURN ROUND(EXTRACT(EPOCH FROM (v_fin_actual - v_fin_anterior)) / 86400.0);
END;
$$ LANGUAGE plpgsql;


-- ============================================================
-- HU-68 — Cálculo del Índice de Recurrencia
-- ============================================================

-- Función de apoyo para obtener el índice de un usuario específico
CREATE OR REPLACE FUNCTION fn_calcular_indice_recurrencia(p_id_usuario BIGINT)
RETURNS INTEGER AS $$
DECLARE
    v_solicitudes INTEGER;
    v_facturas    INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_solicitudes
    FROM solicitud_servicio
    WHERE id_usuario = p_id_usuario
      AND fecha_hora_cierre IS NOT NULL
      AND fecha_hora_cierre >= CURRENT_DATE - INTERVAL '1 year';

    SELECT COUNT(*) INTO v_facturas
    FROM factura
    WHERE id_usuario = p_id_usuario
      AND saldo_factura <= 0
      AND fecha_emision >= CURRENT_DATE - INTERVAL '1 year';

    RETURN v_solicitudes + v_facturas;
END;
$$ LANGUAGE plpgsql;

-- Procedimiento batch para actualizar de forma masiva a todos los usuarios
CREATE OR REPLACE PROCEDURE sp_actualizar_indices_recurrencia()
LANGUAGE plpgsql AS $$
DECLARE
    v_usuario RECORD;
BEGIN
    FOR v_usuario IN SELECT id_usuario FROM usuario LOOP
        UPDATE usuario
        SET indice_recurrencia = fn_calcular_indice_recurrencia(v_usuario.id_usuario)
        WHERE id_usuario = v_usuario.id_usuario;
    END LOOP;
    RAISE NOTICE 'Índices de recurrencia actualizados para todos los usuarios.';
END;
$$;


-- ============================================================
-- HU-69 — Beneficios por Fidelidad
-- ============================================================

-- Función para catalogar el nivel del usuario según su índice
CREATE OR REPLACE FUNCTION fn_categoria_fidelidad(p_id_usuario BIGINT)
RETURNS VARCHAR AS $$
DECLARE
    v_indice INTEGER;
BEGIN
    SELECT indice_recurrencia INTO v_indice
    FROM usuario WHERE id_usuario = p_id_usuario;

    IF v_indice IS NULL THEN
        RETURN 'Regular';
    END IF;

    IF v_indice >= 6 THEN
        RETURN 'Preferencial';
    ELSIF v_indice >= 3 THEN
        RETURN 'Frecuente';
    ELSE
        RETURN 'Regular';
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Función de apoyo para asignar el porcentaje de descuento respectivo
CREATE OR REPLACE FUNCTION fn_descuento_fidelidad(p_categoria VARCHAR)
RETURNS NUMERIC AS $$
BEGIN
    RETURN CASE p_categoria
        WHEN 'Preferencial' THEN 10.0
        WHEN 'Frecuente'    THEN 5.0
        ELSE 0.0
    END;
END;
$$ LANGUAGE plpgsql;

-- Procedimiento batch para actualizar categorías a nivel general
CREATE OR REPLACE PROCEDURE sp_actualizar_categorias_fidelidad()
LANGUAGE plpgsql AS $$
DECLARE
    v_usuario RECORD;
BEGIN
    FOR v_usuario IN SELECT id_usuario FROM usuario LOOP
        UPDATE usuario
        SET categoria_fidelidad = fn_categoria_fidelidad(v_usuario.id_usuario)
        WHERE id_usuario = v_usuario.id_usuario;
    END LOOP;
    RAISE NOTICE 'Categorías de fidelidad actualizadas para todos los usuarios.';
END;
$$;


-- ============================================================
-- HU-64 — Detección de Mayoría de Edad
-- ============================================================

-- Procedimiento batch para purgar o exigir requisitos según la edad (18 años)
CREATE OR REPLACE PROCEDURE sp_detectar_mayoria_edad()
LANGUAGE plpgsql AS $$
DECLARE
    v_afectados INTEGER;
BEGIN
    UPDATE beneficiario_familiar
    SET esquema_vacunacion                 = 'No Aplica',
        centro_educacion_inicial           = 'No Aplica',
        constancia_estudios_universitarios = 'Pendiente',
        certificado_solteria               = 'Pendiente'
    WHERE EXTRACT(YEAR FROM age(fecha_nacimiento)) >= 18
      AND (esquema_vacunacion <> 'No Aplica' OR centro_educacion_inicial <> 'No Aplica');

    GET DIAGNOSTICS v_afectados = ROW_COUNT;
    RAISE NOTICE 'Beneficiarios que pasaron a mayoría de edad: %', v_afectados;
END;
$$;


-- ============================================================
-- HU-71 — Actualización Masiva de Tasas (BCV)
-- ============================================================

-- Función para obtener la tasa oficial más reciente
CREATE OR REPLACE FUNCTION fn_tasa_bcv_actual()
RETURNS NUMERIC AS $$
DECLARE
    v_tasa NUMERIC;
BEGIN
    SELECT monto_oficial INTO v_tasa
    FROM tasa_cambio_bcv
    ORDER BY fecha_hora_vigencia DESC
    LIMIT 1;
    RETURN v_tasa;
END;
$$ LANGUAGE plpgsql;

-- Procedimiento para registrar de manera ordenada una nueva tasa
CREATE OR REPLACE PROCEDURE sp_sincronizar_tasa_bcv(p_nueva_tasa NUMERIC)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO tasa_cambio_bcv (fecha_hora_vigencia, monto_oficial)
    VALUES (CURRENT_TIMESTAMP, p_nueva_tasa);
    RAISE NOTICE 'Tasa BCV registrada: % Bs por dólar (vigencia %).',
        p_nueva_tasa, CURRENT_TIMESTAMP;
END;
$$;


-- ============================================================
-- HU-72 — Protocolo de Limpieza (Soft-Delete)
-- ============================================================

-- Procedimiento batch para archivar registros inactivos por más de un año
CREATE OR REPLACE PROCEDURE sp_limpieza_soft_delete()
LANGUAGE plpgsql AS $$
DECLARE
    v_benef INTEGER;
    v_acom  INTEGER;
BEGIN
    UPDATE beneficiario_familiar b
    SET estatus_registro = 'Archivado'
    WHERE b.estatus_registro <> 'Archivado'
      AND NOT EXISTS (
            SELECT 1 FROM historial_fechas_cobertura h
            WHERE h.ci_familiar = b.ci_familiar
              AND (h.fecha_fin IS NULL OR h.fecha_fin >= CURRENT_DATE - INTERVAL '1 year')
      )
      AND EXISTS (
            SELECT 1 FROM historial_fechas_cobertura h2 WHERE h2.ci_familiar = b.ci_familiar
      );
    GET DIAGNOSTICS v_benef = ROW_COUNT;

    UPDATE acompanante_temporal a
    SET estatus_registro = 'Archivado'
    WHERE a.estatus_registro <> 'Archivado'
      AND a.fecha_fin_acceso IS NOT NULL
      AND a.fecha_fin_acceso < CURRENT_DATE - INTERVAL '1 year';
    GET DIAGNOSTICS v_acom = ROW_COUNT;

    RAISE NOTICE 'Archivados -> beneficiarios: %, acompañantes: %', v_benef, v_acom;
END;
$$;


-- ============================================================
-- HU-70 — Cierre Masivo Mensual de Facturación
-- ============================================================
CREATE SEQUENCE IF NOT EXISTS seq_factura_cierre START 1;

-- Procedimiento batch para consolidar consumos pendientes en facturas de cierre
CREATE OR REPLACE PROCEDURE sp_cierre_mensual_facturas()
LANGUAGE plpgsql AS $$
DECLARE
    v_folio       RECORD;
    v_total       NUMERIC(15,2);
    v_contador    INTEGER := 0;
    v_correlativo INTEGER;
BEGIN
    FOR v_folio IN
        SELECT ec.numero_folio, ec.id_usuario
        FROM estado_cuenta ec
        WHERE NOT EXISTS (
            SELECT 1 FROM factura f WHERE f.numero_folio = ec.numero_folio
        )
    LOOP
        SELECT COALESCE(SUM(cantidad * precio_unitario + impuestos_ley), 0)
        INTO v_total
        FROM item_consumo
        WHERE numero_folio = v_folio.numero_folio;

        IF v_total > 0 THEN
            v_contador := v_contador + 1;
            v_correlativo := nextval('seq_factura_cierre');
            INSERT INTO factura (numero_control, id_usuario, numero_folio, fecha_emision, saldo_factura)
            VALUES ('FAC-AUTO-' || v_correlativo, v_folio.id_usuario, v_folio.numero_folio, CURRENT_DATE, v_total);
        END IF;
    END LOOP;

    RAISE NOTICE 'Cierre mensual completado. Facturas generadas: %', v_contador;
END;
$$;
