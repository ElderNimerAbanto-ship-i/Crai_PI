package Crai.crai.dto;

public record AuthResponse(
        String token,
        String username,
        String rol,
        Long idPersona,
        String nombrePersona
) {
}
