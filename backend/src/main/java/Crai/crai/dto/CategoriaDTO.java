package Crai.crai.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaDTO(
        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long idCategoria,

        @NotBlank(message = "el nombre es obligatorio")
        @Size(max = 150, message = "el nombre no puede superar 150 caracteres")
        String nombre
) {
}
