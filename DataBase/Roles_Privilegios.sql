-- ==============================================================================
-- SCRIPT DE SEGURIDAD LÓGICA Y ROLES (RBAC) - POSTGRESQL
-- Módulos 1 al 4: Identidad, Vínculos, Infraestructura y Seguridad
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- PASO 1: CREACIÓN DE ROLES DE GRUPO (Sin permisos de login, solo agrupan privilegios)
-- ------------------------------------------------------------------------------
-- [Ref: UCAB- SERVICES Grupo 12 (1).docx - Nivel 1, Nivel 3 y Nivel 7]
CREATE ROLE rol_usuario_comunidad;      -- Para Estudiantes, Profesores y Administrativos
CREATE ROLE rol_admin_infraestructura;  -- Para Administradores Centrales y de Sede
CREATE ROLE rol_auditor_seguridad;      -- Para Administradores de TI y Ciberseguridad

-- ------------------------------------------------------------------------------
-- PASO 2: ASIGNACIÓN DE PRIVILEGIOS POR ROL Y MÓDULO
-- ------------------------------------------------------------------------------

-- A. Privilegios: ROL USUARIO DE LA COMUNIDAD
-- Módulo 1 y 2: Pueden leer el catálogo académico y sus perfiles
GRANT SELECT ON usuario, periodo_vinculacion, estudiante, profesor, personal_administrativo, egresado TO rol_usuario_comunidad;
GRANT SELECT ON curso_seccion, imparte, inscribe, beca, preparaduria TO rol_usuario_comunidad;

-- Módulo 3: Pueden gestionar (Crear, Leer y Modificar) sus vínculos familiares y acompañantes
GRANT SELECT, INSERT, UPDATE ON beneficiario_familiar, historial_fechas_cobertura, acompanante_temporal TO rol_usuario_comunidad;
-- Nota de Seguridad: Se prohíbe el DELETE. El borrado es lógico (Triggers) o por Batch.

-- Módulo 4: Solo pueden leer la infraestructura para saber dónde son sus clases o eventos
GRANT SELECT ON sede, edificacion, espacio_fisico, recurso_tecnologico TO rol_usuario_comunidad;


-- B. Privilegios: ROL ADMINISTRADOR DE INFRAESTRUCTURA
-- Módulo 4: Control total (CRUD) sobre la estructura geográfica y física
GRANT SELECT, INSERT, UPDATE, DELETE ON sede, edificacion, espacio_fisico, recurso_tecnologico TO rol_admin_infraestructura;
-- Permiso de lectura sobre usuarios para identificar quién es el responsable de un espacio
GRANT SELECT ON usuario TO rol_admin_infraestructura;


-- C. Privilegios: ROL AUDITOR DE SEGURIDAD
-- Módulo 1 (Seguridad): Auditoría inmutable. Solo lectura para monitorear, jamás modificar.
GRANT SELECT ON usuario, historial_sesion TO rol_auditor_seguridad;

-- ------------------------------------------------------------------------------
-- PASO 3: PRIVILEGIOS SOBRE SECUENCIAS (Vital para los campos BIGSERIAL)
-- ------------------------------------------------------------------------------
-- Si los usuarios van a hacer INSERTs en tablas con IDs automáticos, necesitan permisos sobre las secuencias
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO rol_usuario_comunidad;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO rol_admin_infraestructura;

-- ------------------------------------------------------------------------------
-- PASO 4: CREACIÓN DE CUENTAS DE USUARIO REALES (Con permisos de Login)
-- ------------------------------------------------------------------------------
-- Usuarios de prueba basados en tus inserts.txt
CREATE USER db_carlos_estudiante WITH LOGIN PASSWORD 'Ucab2026.Est*';
CREATE USER db_marlene_profesora WITH LOGIN PASSWORD 'Ucab2026.Prof*';
CREATE USER db_roberto_admin_sede WITH LOGIN PASSWORD 'Ucab2026.Infra*';
CREATE USER db_auditor_ti_admin WITH LOGIN PASSWORD 'Ucab2026.Audit*';

-- ------------------------------------------------------------------------------
-- PASO 5: ASIGNACIÓN DE USUARIOS A SUS RESPECTIVOS ROLES (Grupos)
-- ------------------------------------------------------------------------------
GRANT rol_usuario_comunidad TO db_carlos_estudiante;
GRANT rol_usuario_comunidad TO db_marlene_profesora;
GRANT rol_admin_infraestructura TO db_roberto_admin_sede;
GRANT rol_auditor_seguridad TO db_auditor_ti_admin;


-- ============================================================
-- HU-60 — Control de Acceso por Rol (RBAC)
-- Un profesor solo ve a los estudiantes de los cursos que imparte.
-- (Cuentas de login y datos de prueba en archivos respectivos.)
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

-- ==============================================================================
-- FIN DEL SCRIPT DE SEGURIDAD
-- ==============================================================================
