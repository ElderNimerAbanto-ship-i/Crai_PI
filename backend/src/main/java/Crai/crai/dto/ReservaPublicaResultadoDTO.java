package Crai.crai.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ReservaPublicaResultadoDTO(
        Long idReservaSala,

        String nombreSala,

        LocalDate fecha,

        LocalTime horaInicio,

        LocalTime horaFin,

        Integer duracionMinutos,

        String actividad,

        EstudianteValidadoDTO responsable,

        List<EstudianteValidadoDTO> integrantes,

        String mensaje
) {
}
