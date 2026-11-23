package Crai.crai.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaSalaDTO(
        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long idReservaSala,

        @NotNull(message = "la fecha de reserva es obligatoria")
        @Schema(type = "string", format = "date", example = "2026-10-25", description = "Formato yyyy-MM-dd")
        LocalDate fechaReserva,

        @NotNull(message = "la hora de inicio es obligatoria")
        @Schema(type = "string", example = "08:00:00", description = "Formato HH:mm:ss")
        LocalTime horaInicio,

        @NotNull(message = "la hora de fin es obligatoria")
        @Schema(type = "string", example = "10:00:00", description = "Formato HH:mm:ss")
        LocalTime horaFin,

        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        String estado,

        @NotNull(message = "la sala es obligatoria")
        Long idSala,

        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        String nombreSala,

        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long totalIntegrantes,

        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        String codigoResponsable,

        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        String nombreResponsable,

        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        String actividad,

        @Parameter(hidden = true)
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Integer duracionMinutos
) {
}
