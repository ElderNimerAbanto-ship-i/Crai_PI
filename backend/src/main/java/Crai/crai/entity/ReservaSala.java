package Crai.crai.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reserva_sala",schema = "CRAI")
public class ReservaSala {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservaSala;

    @Column(name = "FECHA_RESERVA")
    private LocalDate fechaReserva;

    @Column(name = "HORA_INICIO")
    private LocalTime horaInicio;

    @Column(name = "HORA_FIN")
    private LocalTime horaFin;

    @Column(name = "ESTADO", length = 1)
    private String estado;

    @ManyToOne
    @JoinColumn(name = "ID_SALA", nullable = false)
    private Sala sala;

    @ManyToOne
    @JoinColumn(name = "ID_RESPONSABLE")
    private Usuario responsable;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTIVIDAD", length = 30)
    private ActividadAcademica actividad;

    @Column(name = "DURACION_MINUTOS")
    private Integer duracionMinutos;

    @Column(name = "FECHA_REGISTRO")
    private LocalDateTime fechaRegistro;

    public ReservaSala() {
    }

    public ReservaSala(Long idReservaSala, LocalDate fechaReserva, LocalTime horaInicio, LocalTime horaFin, String estado, Sala sala, Usuario responsable, ActividadAcademica actividad, Integer duracionMinutos, LocalDateTime fechaRegistro) {
        this.idReservaSala = idReservaSala;
        this.fechaReserva = fechaReserva;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
        this.sala = sala;
        this.responsable = responsable;
        this.actividad = actividad;
        this.duracionMinutos = duracionMinutos;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getIdReservaSala() {
        return idReservaSala;
    }

    public void setIdReservaSala(Long idReservaSala) {
        this.idReservaSala = idReservaSala;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Usuario getResponsable() {
        return responsable;
    }

    public void setResponsable(Usuario responsable) {
        this.responsable = responsable;
    }

    public ActividadAcademica getActividad() {
        return actividad;
    }

    public void setActividad(ActividadAcademica actividad) {
        this.actividad = actividad;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
