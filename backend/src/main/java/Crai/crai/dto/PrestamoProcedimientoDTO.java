package Crai.crai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PrestamoProcedimientoDTO(
        @NotBlank(message = "el codigo universitario es obligatorio")
        String codigoUni,

        @NotNull(message = "el ejemplar es obligatorio")
        Long idEjemplar,

        @NotNull(message = "los dias de prestamo son obligatorios")
        Integer dias
) {
}
