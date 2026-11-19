package Crai.crai.dto;

public record ResumenDTO(
        long totalLibros,

        long totalEjemplares,

        long ejemplaresDisponibles,

        long totalUsuarios,

        long prestamosActivos,

        long prestamosVencidos,

        long reservasHoy,

        long ingresosHoy
) {
}
