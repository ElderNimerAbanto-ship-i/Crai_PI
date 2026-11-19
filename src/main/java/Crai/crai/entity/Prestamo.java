package Crai.crai.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "prestamo",schema = "CRAI")
public class Prestamo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPrestamo;

    @Column(name = "FECHA_PRESTAMO")
    private LocalDateTime fechaPrestamo;

    @Column(name = "FECHA_VENCIMIENTO")
    private LocalDateTime fechaVencimiento;

    @Column(name = "FECHA_DEVOLUCION")
    private LocalDateTime fechaDevolucion;

    @Column(name = "ESTADO", length = 1)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "ID_PERSONA", nullable = false)
    private Persona persona;

    public Prestamo() {
    }

    public Prestamo(Long idPrestamo, LocalDateTime fechaPrestamo, LocalDateTime fechaVencimiento, LocalDateTime fechaDevolucion, String estado, Persona persona) {
        this.idPrestamo = idPrestamo;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaDevolucion = fechaDevolucion;
        this.estado = estado;
        this.persona = persona;
    }

    public Long getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(Long idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public LocalDateTime getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDateTime fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDateTime getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDateTime fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }
}
