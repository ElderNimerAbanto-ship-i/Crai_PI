package Crai.crai.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReservaIntegranteDTO(
        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long idIntegrante,

        @NotBlank(message = "el codigo universitario es obligatorio")
        String codigoUni,

        @NotNull(message = "la reserva de sala es obligatoria")
        Long idReservaSala,

        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        String nombrePersona
) {
}
