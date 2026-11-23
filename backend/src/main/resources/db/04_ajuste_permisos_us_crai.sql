-- =====================================================================
-- PASO 4b · Ejecutar como CRAI SOLO si ya ejecutaste la versión anterior
-- de 01_usuario_aplicacion_us_crai.sql. Deja los permisos iguales a la
-- versión actual (mínimo privilegio ajustado a lo que hace el código).
-- =====================================================================
REVOKE INSERT ON CRAI.prestamo         FROM US_CRAI; -- solo el SP inserta préstamos
REVOKE INSERT ON CRAI.detalle_prestamo FROM US_CRAI; -- solo el SP inserta detalles
REVOKE UPDATE ON CRAI.detalle_prestamo FROM US_CRAI; -- el backend solo lo lee
REVOKE UPDATE ON CRAI.persona          FROM US_CRAI; -- persona solo se crea
REVOKE UPDATE ON CRAI.control_ingreso  FROM US_CRAI; -- los ingresos no se editan

SELECT table_name, privilege FROM user_tab_privs_made
 WHERE grantee = 'US_CRAI' ORDER BY table_name, privilege;
