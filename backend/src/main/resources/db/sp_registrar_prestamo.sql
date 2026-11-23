-- =====================================================================
-- Ejecutar CONECTADO COMO CRAI (dueño de las tablas), NO como US_CRAI.
-- =====================================================================
CREATE OR REPLACE PROCEDURE sp_registrar_prestamo (
    PV_CODIGO_UNI  IN  VARCHAR2,
    PV_ID_EJEMPLAR IN  NUMBER,
    PV_DIAS        IN  NUMBER,
    PV_ID_PRESTAMO OUT NUMBER) IS
  VN_ID_PERSONA NUMBER;
  VN_ESTADO_EJ  ejemplar.estado%TYPE;
  VN_VENCIDOS   NUMBER;
  VN_ACTIVOS    NUMBER;
BEGIN
  -- 1. Días de préstamo
  IF PV_DIAS IS NULL OR PV_DIAS NOT BETWEEN 1 AND 30 THEN
    RAISE_APPLICATION_ERROR(-20011, 'Los dias de prestamo deben estar entre 1 y 30');
  END IF;

  -- 2. Usuario por código universitario (estado_acceso es NUMBER)
  BEGIN
    SELECT u.id_persona
      INTO VN_ID_PERSONA
      FROM usuario u
     WHERE UPPER(TRIM(u.codigo_uni)) = UPPER(TRIM(PV_CODIGO_UNI))
       AND u.estado_acceso = 1;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      RAISE_APPLICATION_ERROR(-20015, 'El codigo universitario no existe o no tiene acceso habilitado.');
  END;

  -- 3. Ejemplar existente y disponible
  BEGIN
    SELECT estado INTO VN_ESTADO_EJ
      FROM ejemplar
     WHERE id_ejemplar = PV_ID_EJEMPLAR;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      RAISE_APPLICATION_ERROR(-20018, 'El ejemplar indicado no existe.');
  END;
  IF UPPER(VN_ESTADO_EJ) <> 'DISPONIBLE' THEN
    RAISE_APPLICATION_ERROR(-20012, 'El ejemplar no esta disponible para prestamo.');
  END IF;

  -- 4. Reglas del usuario (las que anuncia la pantalla: máx. 3 activos y ninguno vencido)
  SELECT COUNT(*) INTO VN_VENCIDOS
    FROM prestamo
   WHERE id_persona = VN_ID_PERSONA AND estado = 'A' AND fecha_vencimiento < SYSDATE;
  IF VN_VENCIDOS > 0 THEN
    RAISE_APPLICATION_ERROR(-20016, 'El usuario tiene prestamos vencidos pendientes de devolucion.');
  END IF;

  SELECT COUNT(*) INTO VN_ACTIVOS
    FROM prestamo
   WHERE id_persona = VN_ID_PERSONA AND estado = 'A';
  IF VN_ACTIVOS >= 3 THEN
    RAISE_APPLICATION_ERROR(-20017, 'El usuario alcanzo el maximo de 3 prestamos activos.');
  END IF;

  -- 5. Cabecera
  INSERT INTO prestamo (fecha_prestamo, fecha_vencimiento, estado, id_persona)
  VALUES (SYSDATE, SYSDATE + PV_DIAS, 'A', VN_ID_PERSONA)
  RETURNING id_prestamo INTO PV_ID_PRESTAMO;

  -- 6. Detalle (dispara trg_detalle_prestamo_bi si existe)
  INSERT INTO detalle_prestamo (id_prestamo, id_ejemplar)
  VALUES (PV_ID_PRESTAMO, PV_ID_EJEMPLAR);

  -- 7. Marcar PRESTADO (inofensivo si el trigger ya lo hizo)
  UPDATE ejemplar
     SET estado = 'PRESTADO'
   WHERE id_ejemplar = PV_ID_EJEMPLAR
     AND UPPER(estado) = 'DISPONIBLE';
END sp_registrar_prestamo;
/

-- IMPRESCINDIBLE: el backend se conecta como US_CRAI
GRANT EXECUTE ON CRAI.sp_registrar_prestamo TO US_CRAI;

-- Debe devolver VALID (si dice INVALID: SHOW ERRORS PROCEDURE sp_registrar_prestamo)
SELECT owner, object_name, status FROM all_objects WHERE object_name = 'SP_REGISTRAR_PRESTAMO';