package Crai.crai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reserva_integrante",schema = "CRAI")
public class ReservaIntegrante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIntegrante;

    @Column(name = "CODIGO_UNI", length = 20, nullable = false)
    private String codigoUni;

    @ManyToOne
    @JoinColumn(name = "ID_RESERVA_SALA", nullable = false)
    private ReservaSala reservaSala;

    public ReservaIntegrante() {
    }

    public ReservaIntegrante(Long idIntegrante, String codigoUni, ReservaSala reservaSala) {
        this.idIntegrante = idIntegrante;
        this.codigoUni = codigoUni;
        this.reservaSala = reservaSala;
    }

    public Long getIdIntegrante() {
        return idIntegrante;
    }

    public void setIdIntegrante(Long idIntegrante) {
        this.idIntegrante = idIntegrante;
    }

    public String getCodigoUni() {
        return codigoUni;
    }

    public void setCodigoUni(String codigoUni) {
        this.codigoUni = codigoUni;
    }

    public ReservaSala getReservaSala() {
        return reservaSala;
    }

    public void setReservaSala(ReservaSala reservaSala) {
        this.reservaSala = reservaSala;
    }
}
