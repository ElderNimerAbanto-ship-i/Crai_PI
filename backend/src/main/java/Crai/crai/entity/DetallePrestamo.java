package Crai.crai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_prestamo",schema = "CRAI")
public class DetallePrestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalle;

    @ManyToOne
    @JoinColumn(name = "ID_PRESTAMO", nullable = false)
    private Prestamo prestamo;

    @ManyToOne
    @JoinColumn(name = "ID_EJEMPLAR", nullable = false)
    private Ejemplar ejemplar;

    public DetallePrestamo() {
    }

    public DetallePrestamo(Long idDetalle, Prestamo prestamo, Ejemplar ejemplar) {
        this.idDetalle = idDetalle;
        this.prestamo = prestamo;
        this.ejemplar = ejemplar;
    }

    public Long getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Long idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public Ejemplar getEjemplar() {
        return ejemplar;
    }

    public void setEjemplar(Ejemplar ejemplar) {
        this.ejemplar = ejemplar;
    }
}
