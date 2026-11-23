package Crai.crai.dto;

import java.time.LocalTime;

public record HorarioSalaDTO(
        LocalTime horaInicio,
        LocalTime horaFin,
        String estado
) {
}
