package Crai.crai.exception;

import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.hibernate.JDBCException;
import org.springframework.dao.DataAccessException;
import java.sql.SQLException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String mensaje, Map<String, String> errores) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", mensaje);
        if (errores != null) {
            body.put("errores", errores);
        }
        return new ResponseEntity<>(body, status);
    }

    // Login: usuario o contraseña incorrectos
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> credencialesInvalidas(BadCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos", null);
    }

    // Login: usuario con acceso deshabilitado (estadoAcceso = 0)
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, Object>> usuarioDeshabilitado(DisabledException ex) {
        return build(HttpStatus.FORBIDDEN, "El usuario tiene el acceso deshabilitado", null);
    }
    // Errores de Oracle que llegan envueltos por JPA/Hibernate (triggers, procedimientos, etc.).
    // Antes no se controlaban: Spring hacia forward a /error, Security respondia 401 y el
    // frontend cerraba la sesion y mandaba al /login.
    @ExceptionHandler({DataAccessException.class, JDBCException.class})
    public ResponseEntity<Map<String, Object>> errorBaseDatos(Exception ex) {
        SQLException sql = buscarSqlException(ex);
        if (sql != null) {
            int codigo = sql.getErrorCode();
            // ORA-20020: trg_ingreso_alterna (dos ENTRADA seguidas)
            if (codigo == 20020) {
                return build(HttpStatus.CONFLICT, "El usuario ya se encuentra registrado", null);
            }
            // ORA-20000 a ORA-20999: RAISE_APPLICATION_ERROR -> regla de negocio, se muestra su texto
            if (codigo >= 20000 && codigo <= 20999) {
                return build(HttpStatus.CONFLICT, mensajeOracle(sql.getMessage()), null);
            }
        }
        LoggerFactory.getLogger(GlobalExceptionHandler.class).error("Error de base de datos", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error en la base de datos al procesar la operacion", null);
    }

    private SQLException buscarSqlException(Throwable ex) {
        Throwable t = ex;
        while (t != null) {
            if (t instanceof SQLException s) return s;
            t = t.getCause();
        }
        return null;
    }

    // "ORA-20017: El usuario alcanzo...\nORA-06512: at ..." -> "El usuario alcanzo..."
    private String mensajeOracle(String original) {
        if (original == null) return "Operacion rechazada por la base de datos";
        String linea = original.split("\\R")[0];
        return linea.replaceFirst("^ORA-\\d+:\\s*", "").trim();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler({DuplicateResourceException.class, BusinessRuleException.class})
    public ResponseEntity<Map<String, Object>> conflicto(RuntimeException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> datoInvalido(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    // Errores de @Valid (cuerpo JSON y formularios multipart)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> validacion(BindException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.put(e.getField(), e.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "Datos invalidos", errores);
    }

    // JSON mal formado, fechas u horas con formato incorrecto, valores de enum invalidos
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonInvalido(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST,
                "El cuerpo de la peticion es invalido. Revisa el formato de fechas (yyyy-MM-dd), horas (HH:mm:ss) y los valores permitidos", null);
    }

    // Ej: borrar una categoria que todavia tiene libros
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integridad(DataIntegrityViolationException ex) {
        return build(HttpStatus.CONFLICT,
                "La operacion viola una restriccion de datos (registro relacionado o duplicado)", null);
    }

    // Cualquier otro error: se devuelve el mensaje real (y queda en el log) en vez de un 500 mudo
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> estadoInvalido(IllegalStateException ex) {
        LoggerFactory.getLogger(GlobalExceptionHandler.class).error("Error interno", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> archivoGrande(MaxUploadSizeExceededException ex) {
        return build(HttpStatus.PAYLOAD_TOO_LARGE, "La imagen supera el tamano maximo permitido (1MB)", null);
    }
}
