# UCAB-Services - Segunda Entrega (Grupo 12)

Sistema integral de gestión de servicios (OLTP) universitario. 

## 🛠 Ambiente de Desarrollo y Tecnologías
* **Lenguaje:** Java 17
* **Framework:** Spring Boot 3.x (Spring Data JPA, Spring Data JDBC, Thymeleaf)
* **Sistema Gestor de Base de Datos:** PostgreSQL 18.x
* **Entorno de Desarrollo:** Visual Studio Code / Eclipse / IntelliJ
* **Herramienta de Reportes:** [Indicar si es JasperReports, jsreport, etc.]

## Guía de Despliegue y Ejecución

### Paso 1: Configuración de la Base de Datos
1. Iniciar el servidor de PostgreSQL (puerto 5432).
2. Crear una base de datos vacía llamada `ucab_services_db`.
3. Ejecutar los scripts ubicados en la carpeta `/database` en el siguiente orden estricto:
   - `1_DDL_Creates.sql`: Para generar la estructura y restricciones.
   - `2_PLSQL.sql`: Para automatizar la integridad (Triggers, Funciones y SPs).
   - `3_DCL_Seguridad.sql`: Para configurar la seguridad, roles y privilegios.
   - `4_DML_Inserts.sql`: Para inyectar los datos de prueba obligatorios.

### Paso 2: Configuración de la Aplicación Web
1. Importar la carpeta `/ucabservices` como un proyecto Maven en el IDE de preferencia.
2. Verificar en el archivo `src/main/resources/application.properties` que las credenciales de conexión a PostgreSQL coincidan con el entorno local:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/ucab_services_db
   spring.datasource.username=tu_usuario_local
   spring.datasource.password=tu_clave_local

### Paso 3: Ejecución
1. Ejecutar la clase principal ServicesApplication.java.
2. Una vez inicializado Tomcat en el puerto 8080, abrir un navegador web.
3. Acceder a http://localhost:8080/login para visualizar el sistema.