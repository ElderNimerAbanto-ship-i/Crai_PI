-- =====================================================================
-- PASO 6 · Demostración de mínimo privilegio (para la sustentación)
-- Ejecutar conectado como US_CRAI, sentencia por sentencia (Ctrl+Enter).
-- =====================================================================

-- ¿Quién soy?
SELECT USER FROM dual;                                   -- US_CRAI

-- ---------- LO QUE SÍ PUEDE (lo que usa la aplicación) --------------
SELECT COUNT(*) FROM CRAI.libro;                         -- OK
SELECT * FROM CRAI.vw_libros_mas_prestados;              -- OK (vista)

-- Permisos que tiene, y de quién los recibió
SELECT owner, table_name, privilege
  FROM user_tab_privs
 ORDER BY table_name, privilege;

-- ---------- LO QUE NO PUEDE (cada una debe fallar) ------------------
DROP TABLE CRAI.libro;                -- ORA-01031: no puede destruir el esquema
CREATE TABLE prueba (x NUMBER);       -- ORA-01031: no puede crear objetos
ALTER TABLE CRAI.libro ADD (x NUMBER);-- ORA-01031: no puede alterar estructura
DELETE FROM CRAI.prestamo;            -- ORA-01031: el historial de préstamos
DELETE FROM CRAI.control_ingreso;     -- ORA-01031: y de ingresos es imborrable
INSERT INTO CRAI.prestamo (fecha_prestamo, fecha_vencimiento, estado, id_persona)
VALUES (SYSDATE, SYSDATE + 3, 'A', 1);  -- ORA-01031: un préstamo solo nace por el SP

-- ---------- EL CAMINO CORRECTO: el procedimiento ---------------------
-- (registra de verdad un préstamo; usa un código y un ejemplar reales)
-- VARIABLE id NUMBER
-- EXEC CRAI.sp_registrar_prestamo('202310123', 1, 3, :id);
-- PRINT id
UPDATE CRAI.vw_libros_mas_prestados SET titulo = 'x';  -- ORA-01031: vista solo lectura

-- Por si alguna sentencia anterior hubiera pasado
ROLLBACK;
