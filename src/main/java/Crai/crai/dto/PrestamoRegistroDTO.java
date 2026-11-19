package Crai.crai.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PrestamoRegistroDTO(
        @NotBlank(message = "el codigo universitario es obligatorio")
        String codigoUni,

        @NotNull(message = "el ejemplar es obligatorio")
        Long idEjemplar,

        @NotNull(message = "los dias de prestamo son obligatorios")
        @Min(value = 1, message = "minimo 1 dia")
        @Max(value = 30, message = "maximo 30 dias")
        Integer diasPrestamo
) {
}
