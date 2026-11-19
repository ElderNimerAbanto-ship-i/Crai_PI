package Crai.crai.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record EjemplarDTO(
        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long idEjemplar,

        @Pattern(regexp = "DISPONIBLE|PRESTADO|MANTENIMIENTO", message = "el estado debe ser DISPONIBLE, PRESTADO o MANTENIMIENTO")
        @Schema(example = "DISPONIBLE")
        String estado,

        String isbn,

        @NotNull(message = "el libro es obligatorio")
        Long idLibro,

        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        String tituloLibro
) {
}
