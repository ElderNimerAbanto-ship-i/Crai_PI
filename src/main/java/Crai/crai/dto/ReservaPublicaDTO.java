package Crai.crai.dto;

import Crai.crai.entity.ActividadAcademica;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;
import java.util.List;

public record ReservaPublicaDTO(
        @Schema(description = "Sala elegida (opcional). Si no se envia, el sistema asigna una automaticamente")
        Long idSala,

        @Schema(type = "string", example = "10:00:00", description = "Hora de inicio elegida (HH:mm:ss). Obligatoria si se envia idSala")
        LocalTime horaInicio,

        @NotBlank(message = "el codigo del responsable es obligatorio")
        String codigoResponsable,

        @NotNull(message = "los integrantes son obligatorios")
        @Size(min = 2, max = 7, message = "el grupo debe tener entre 2 y 7 integrantes ademas del responsable")
        List<@NotBlank(message = "el codigo del integrante no puede estar vacio") String> codigosIntegrantes,

        @NotNull(message = "la duracion es obligatoria")
        @Schema(example = "60", description = "Minutos: 60, 90 o 120")
        Integer duracionMinutos,

        @NotNull(message = "la actividad academica es obligatoria")
        ActividadAcademica actividad,

        @NotNull(message = "debes aceptar las reglas de uso")
        @AssertTrue(message = "debes aceptar las reglas de uso")
        Boolean aceptaReglas
) {
}
