package Crai.crai.dto;

public record UsuarioDTO(
        Long idPersona,

        String username,

        String rol,

        String codigoUni,

        String nombrePersona,

        String email,

        Boolean accesoHabilitado,

        String nombreEscuela
) {
}
