package Crai.crai.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "control_ingreso",schema = "CRAI")
public class ControlIngreso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIngreso;

    @Column(name = "FECHA_HORA")
    private LocalDateTime fechaHora;

    @Column(name = "TIPO_ACCESO", length = 20)
    private String tipoAcceso;

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    public ControlIngreso() {
    }

    public ControlIngreso(Long idIngreso, LocalDateTime fechaHora, String tipoAcceso, Usuario usuario) {
        this.idIngreso = idIngreso;
        this.fechaHora = fechaHora;
        this.tipoAcceso = tipoAcceso;
        this.usuario = usuario;
    }

    public Long getIdIngreso() {
        return idIngreso;
    }

    public void setIdIngreso(Long idIngreso) {
        this.idIngreso = idIngreso;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getTipoAcceso() {
        return tipoAcceso;
    }

    public void setTipoAcceso(String tipoAcceso) {
        this.tipoAcceso = tipoAcceso;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
