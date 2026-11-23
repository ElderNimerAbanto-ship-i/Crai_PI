package Crai.crai.repository;

import Crai.crai.exception.BusinessRuleException;
import Crai.crai.exception.ResourceNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.Types;

@Repository
public class PrestamoProcedimientoRepository {

    // Esquema CRAI porque el backend se conecta como US_CRAI (igual que @Table(schema = "CRAI"))
    private static final String CALL_SP_REGISTRAR_PRESTAMO = "{call CRAI.sp_registrar_prestamo(?, ?, ?, ?)}";

    private static final int ORA_FK_PADRE_NO_EXISTE = 2291;   // ORA-02291
    private static final int ORA_APP_ERROR_MIN = 20000;       // RAISE_APPLICATION_ERROR -20000..-20999
    private static final int ORA_APP_ERROR_MAX = 20999;

    private final JdbcTemplate jdbcTemplate;

    public PrestamoProcedimientoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Ejecuta el procedimiento y devuelve el id_prestamo generado (parametro OUT).
     */
    public Long registrarPrestamo(String codigoUni, Long idEjemplar, Integer dias) {
        try {
            return jdbcTemplate.execute(CALL_SP_REGISTRAR_PRESTAMO, (CallableStatementCallback<Long>) cs -> {
                cs.setString(1, codigoUni.trim());     // PV_CODIGO_UNI
                cs.setLong(2, idEjemplar);             // PV_ID_EJEMPLAR
                cs.setInt(3, dias);                    // PV_DIAS
                cs.registerOutParameter(4, Types.NUMERIC); // PV_ID_PRESTAMO OUT
                cs.execute();
                return cs.getLong(4);
            });
        } catch (DataAccessException ex) {
            throw traducirErrorOracle(ex);
        }
    }

    /**
     * Convierte los errores de Oracle en las excepciones del proyecto,
     * para que GlobalExceptionHandler responda con un mensaje claro.
     */
    private RuntimeException traducirErrorOracle(DataAccessException ex) {
        SQLException sql = buscarSqlException(ex);
        if (sql == null) {
            return ex;
        }
        int codigo = sql.getErrorCode();

        // Errores lanzados con RAISE_APPLICATION_ERROR en el procedimiento o en el trigger
        // Ej: ORA-20015 "El codigo universitario no existe o no tiene acceso habilitado."
        //     ORA-20011 "Los dias de prestamo deben estar entre 1 y 5"
        if (codigo >= ORA_APP_ERROR_MIN && codigo <= ORA_APP_ERROR_MAX) {
            return new BusinessRuleException(limpiarMensaje(sql.getMessage()));
        }
        // El ejemplar no existe (FK de detalle_prestamo). La persona ya la valida el procedimiento.
        if (codigo == ORA_FK_PADRE_NO_EXISTE) {
            return new ResourceNotFoundException("El ejemplar indicado no existe");
        }
        // Procedimiento inexistente / invalido / sin permiso EXECUTE para US_CRAI, tabla no visible, etc.
        // ORA-06550 (PLS-00201, PLS-00905), ORA-00942, ORA-04063, ORA-06575
        if (codigo == 6550 || codigo == 942 || codigo == 4063 || codigo == 6575) {
            return new IllegalStateException(
                    "No se pudo ejecutar CRAI.sp_registrar_prestamo (inexistente, invalido o sin permiso EXECUTE para US_CRAI): "
                            + limpiarMensaje(sql.getMessage()), ex);
        }
        return new IllegalStateException("Error de base de datos al registrar el prestamo: "
                + limpiarMensaje(sql.getMessage()), ex);
    }

    private SQLException buscarSqlException(Throwable ex) {
        Throwable actual = ex;
        while (actual != null) {
            if (actual instanceof SQLException sql) {
                return sql;
            }
            actual = actual.getCause();
        }
        return null;
    }

    /**
     * "ORA-20011: Los dias ... 30\nORA-06512: at ..." -> "Los dias ... 30"
     */
    private String limpiarMensaje(String mensajeOracle) {
        if (mensajeOracle == null) {
            return "Error de regla de negocio en la base de datos";
        }
        String primeraLinea = mensajeOracle.split("\\R", 2)[0];
        return primeraLinea.replaceFirst("^ORA-\\d{5}:\\s*", "").trim();
    }
}
