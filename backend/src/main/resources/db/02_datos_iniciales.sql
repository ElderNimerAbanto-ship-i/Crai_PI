-- =====================================================================
-- PASO 5 · Ejecutar como CRAI. Reemplaza a CRAIU2.sql.
-- =====================================================================

-- 5a. Escuela profesional (va PRIMERO: USUARIO la referencia).
--     Sin ID explícito: lo genera la columna IDENTITY.
INSERT INTO CRAI.escuela_prof (nombre_esc, estado)
VALUES ('Ingeniería de Sistemas', 1);
COMMIT;

-- Anota el id que salió (normalmente 1):
SELECT id_escuela_prof, nombre_esc FROM CRAI.escuela_prof;

-- 5b. El usuario NO se inserta por SQL: la contraseña se guarda con BCrypt.
--     Regístralo con el backend levantado (Swagger o Postman):
--
--     POST http://localhost:8082/crai/api/v1/auth/registro
--     {
--       "nombres": "Jhosmer Smit",
--       "apellidos": "Saldaña Sanchez",
--       "email": "jhosmer@upeu.edu.pe",
--       "codigoUni": "202310123",
--       "username": "jsaldana",
--       "password": "admin123",
--       "rol": "ESTUDIANTE",
--       "idEscuelaProf": 1
--     }

-- 5c. El registro siempre crea ESTUDIANTE; súbelo a ADMIN para entrar
--     al panel de administración:
UPDATE CRAI.usuario SET rol = 'ADMIN' WHERE username = 'jsaldana';
COMMIT;

SELECT u.id_persona, u.username, u.rol, p.email
  FROM CRAI.usuario u JOIN CRAI.persona p ON p.id_persona = u.id_persona;
