package Crai.crai.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SalaDTO(
        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long idSala,

        @NotBlank(message = "el nombre es obligatorio")
        String nombre,

        @NotNull(message = "la capacidad minima es obligatoria")
        @Min(value = 1, message = "la capacidad minima debe ser al menos 1")
        Integer capacidadMin,

        @NotNull(message = "la capacidad maxima es obligatoria")
        @Min(value = 1, message = "la capacidad maxima debe ser al menos 1")
        Integer capacidadMax,

        @Pattern(regexp = "DISPONIBLE|MANTENIMIENTO", message = "el estado debe ser DISPONIBLE o MANTENIMIENTO")
        @Schema(example = "DISPONIBLE")
        String estado
) {
}
