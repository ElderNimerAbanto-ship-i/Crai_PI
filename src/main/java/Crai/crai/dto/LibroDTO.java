package Crai.crai.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LibroDTO(
        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long idLibro,

        @NotBlank(message = "el titulo es obligatorio")
        String titulo,

        @NotBlank(message = "el autor es obligatorio")
        String autor,

        String seccionEspecial,

        @NotNull(message = "la categoria es obligatoria")
        Long idCategoria,

        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        String nombreCategoria,

        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        String imagen,

        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long ejemplaresDisponibles
) {
}
