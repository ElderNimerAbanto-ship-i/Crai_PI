-- =====================================================================
-- PASO 1 · Ejecutar como SYSTEM (SQL Developer: system / tu clave, servicio xe)
-- Crea el dueño del esquema (CRAI) y el usuario de aplicación (US_CRAI).
-- =====================================================================

-- En Oracle XE 18c/21c la conexión al servicio "xe" es el contenedor raíz;
-- sin esta línea CREATE USER CRAI da ORA-65096. (En XE 11g no hace falta.)
-- Antes de ejecutar, reemplaza las dos contraseñas de abajo.
-- La de US_CRAI es la que va en la variable CRAI_DB_PASSWORD del backend.
ALTER SESSION SET "_ORACLE_SCRIPT" = TRUE;

-- Dueño del esquema: crea tablas, vista y procedimiento
CREATE USER CRAI IDENTIFIED BY "Cambiar_Clave_CRAI"
  DEFAULT TABLESPACE users
  QUOTA UNLIMITED ON users;

GRANT CREATE SESSION, CREATE TABLE, CREATE VIEW,
      CREATE PROCEDURE, CREATE SEQUENCE, CREATE TRIGGER TO CRAI;

-- Usuario de aplicación: solo puede conectarse; los permisos sobre
-- los datos se los da CRAI en 01_usuario_aplicacion_us_crai.sql
CREATE USER US_CRAI IDENTIFIED BY "Cambiar_Clave_US_CRAI";
GRANT CREATE SESSION TO US_CRAI;

-- Verificación
SELECT username, account_status FROM dba_users
 WHERE username IN ('CRAI', 'US_CRAI');
