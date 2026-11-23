package Crai.crai.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EscuelaProfDTO(
        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long idEscuelaProf,

        @NotBlank(message = "el nombre de la escuela es obligatorio")
        String nombreEsc,

        @Pattern(regexp = "A|I", message = "el estado debe ser A (activa) o I (inactiva)")
        @Schema(example = "A")
        String estado
) {
}
