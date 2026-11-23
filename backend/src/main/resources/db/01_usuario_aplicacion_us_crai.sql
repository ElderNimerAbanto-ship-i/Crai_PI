-- =====================================================================
-- PASO 4 · Ejecutar como CRAI, DESPUÉS de:
--   (1) 00_system_crear_usuarios.sql
--   (2) levantar el backend una vez (ddl-auto: update crea las tablas)
--   (3) sp_registrar_prestamo.sql
-- Crea la vista y da a US_CRAI solo los permisos que usa la aplicación.
-- =====================================================================

-- Vista con esquema explícito (debe existir en CRAI, no en US_CRAI)
CREATE OR REPLACE VIEW CRAI.vw_libros_mas_prestados AS
SELECT l.id_libro,
       l.titulo,
       COUNT(*) AS total_prestamos,
       RANK() OVER (ORDER BY COUNT(*) DESC) AS posicion
FROM CRAI.detalle_prestamo d
JOIN CRAI.ejemplar e ON e.id_ejemplar = d.id_ejemplar
JOIN CRAI.libro l    ON l.id_libro    = e.id_libro
GROUP BY l.id_libro, l.titulo;

-- Tablas que el backend da de baja (deleteById / delete en los services)
GRANT SELECT, INSERT, UPDATE, DELETE ON CRAI.categoria          TO US_CRAI;
GRANT SELECT, INSERT, UPDATE, DELETE ON CRAI.libro              TO US_CRAI;
GRANT SELECT, INSERT, UPDATE, DELETE ON CRAI.ejemplar           TO US_CRAI;
GRANT SELECT, INSERT, UPDATE, DELETE ON CRAI.sala               TO US_CRAI;
GRANT SELECT, INSERT, UPDATE, DELETE ON CRAI.reserva_sala       TO US_CRAI;
GRANT SELECT, INSERT, UPDATE, DELETE ON CRAI.reserva_integrante TO US_CRAI;
GRANT SELECT, INSERT, UPDATE, DELETE ON CRAI.escuela_prof       TO US_CRAI;

-- Tablas sin borrado desde la aplicación (historial protegido)
GRANT SELECT, INSERT, UPDATE ON CRAI.usuario TO US_CRAI;  -- registro y cambio de acceso
GRANT SELECT, INSERT         ON CRAI.persona TO US_CRAI;  -- solo se crea al registrar
GRANT SELECT, INSERT ON CRAI.control_ingreso TO US_CRAI;  -- cada ENTRADA/SALIDA es un INSERT

-- Préstamos: la API NO inserta directamente. Todo préstamo nuevo pasa por
-- sp_registrar_prestamo, que corre con los permisos de CRAI (AUTHID DEFINER).
GRANT SELECT, UPDATE ON CRAI.prestamo         TO US_CRAI; -- devolver() y renovar()
GRANT SELECT         ON CRAI.detalle_prestamo TO US_CRAI; -- solo lectura

-- Vista: solo lectura
GRANT SELECT ON CRAI.vw_libros_mas_prestados TO US_CRAI;

-- Procedimiento (también está al final de sp_registrar_prestamo.sql)
GRANT EXECUTE ON CRAI.sp_registrar_prestamo TO US_CRAI;

-- No hace falta GRANT sobre secuencias: los ids son columnas IDENTITY
-- y el privilegio INSERT sobre la tabla ya cubre su secuencia ISEQ$$.

-- ---------- Verificación (como CRAI) ---------------------------------
SELECT table_name, privilege
  FROM user_tab_privs_made
 WHERE grantee = 'US_CRAI'
 ORDER BY table_name, privilege;
