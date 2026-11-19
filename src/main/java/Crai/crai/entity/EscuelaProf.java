package Crai.crai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "escuela_prof",schema = "CRAI")
public class EscuelaProf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEscuelaProf;

    @Column(name = "NOMBRE_ESC", length = 100, nullable = false)
    private String nombreEsc;

    @Column(name = "ESTADO", length = 1)
    private String estado;

    public EscuelaProf() {
    }

    public EscuelaProf(Long idEscuelaProf, String nombreEsc, String estado) {
        this.idEscuelaProf = idEscuelaProf;
        this.nombreEsc = nombreEsc;
        this.estado = estado;
    }

    public Long getIdEscuelaProf() {
        return idEscuelaProf;
    }

    public void setIdEscuelaProf(Long idEscuelaProf) {
        this.idEscuelaProf = idEscuelaProf;
    }

    public String getNombreEsc() {
        return nombreEsc;
    }

    public void setNombreEsc(String nombreEsc) {
        this.nombreEsc = nombreEsc;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
