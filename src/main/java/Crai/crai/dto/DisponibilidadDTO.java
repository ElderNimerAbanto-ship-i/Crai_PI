package Crai.crai.dto;

import java.time.LocalTime;

public record DisponibilidadDTO(
        boolean disponible,

        Long idSala,

        String nombreSala,

        LocalTime horaInicio,

        LocalTime horaFin,

        String mensaje
) {
}
