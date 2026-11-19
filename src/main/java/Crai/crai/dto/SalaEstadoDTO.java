package Crai.crai.dto;

import java.time.LocalTime;

public record SalaEstadoDTO(
        Long idSala,

        String nombre,

        Integer capacidadMin,

        Integer capacidadMax,

        String estado,

        LocalTime ocupadaHasta
) {
}
