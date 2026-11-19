package Crai.crai.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record ControlIngresoDTO(
        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long idIngreso,

        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        LocalDateTime fechaHora,

        @NotBlank(message = "el tipo de acceso es obligatorio")
        @Pattern(regexp = "ENTRADA|SALIDA", message = "el tipo de acceso debe ser ENTRADA o SALIDA")
        @Schema(example = "ENTRADA")
        String tipoAcceso,

        @NotBlank(message = "el codigo universitario es obligatorio")
        String codigoUni,

        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        String nombrePersona,

        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        String escuela
) {
}
