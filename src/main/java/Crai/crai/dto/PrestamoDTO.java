package Crai.crai.dto;

import java.time.LocalDateTime;

public record PrestamoDTO(
        Long idPrestamo,

        LocalDateTime fechaPrestamo,

        LocalDateTime fechaVencimiento,

        LocalDateTime fechaDevolucion,

        String estado,

        Long idPersona,

        String nombrePersona
) {
}
