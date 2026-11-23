package Crai.crai.dto;

public record DetallePrestamoDTO(
        Long idDetalle,

        Long idPrestamo,

        Long idEjemplar,

        String isbnEjemplar,

        String tituloLibro
) {
}
