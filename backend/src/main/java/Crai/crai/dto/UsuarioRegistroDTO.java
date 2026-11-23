package Crai.crai.dto;

import Crai.crai.entity.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record UsuarioRegistroDTO(
        @NotBlank(message = "el nombre es obligatorio")
        String nombres,

        @NotBlank(message = "el apellido es obligatorio")
        String apellidos,

        @NotBlank(message = "el email es obligatorio")
        @Email(message = "debe ser un email valido")
        String email,

        @NotBlank(message = "el codigo universitario es obligatorio")
        String codigoUni,

        @NotBlank(message = "el usuario es obligatorio")
        @Size(min = 4, max = 20, message = "el usuario debe tener entre 4 y 20 caracteres")
        String username,

        @NotBlank(message = "el password es obligatorio")
        @Size(min = 6, message = "la contraseña debe tener al menos 6 caracteres")
        String password,

        @NotNull(message = "el rol es obligatorio")
        @Schema(example = "ESTUDIANTE")
        Rol rol,

        Long idEscuelaProf
) {
}
