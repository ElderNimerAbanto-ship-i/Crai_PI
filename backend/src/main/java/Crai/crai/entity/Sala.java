package Crai.crai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sala",schema = "CRAI")
public class Sala {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSala;

    @Column(name = "NOMBRE", length = 50, nullable = false, unique = true)
    private String nombre;

    @Column(name = "CAPACIDAD_MIN")
    private Integer capacidadMin;

    @Column(name = "CAPACIDAD_MAX")
    private Integer capacidadMax;

    @Column(name = "ESTADO", length = 30)
    private String estado;

    public Sala() {
    }

    public Sala(Long idSala, String nombre, Integer capacidadMin, Integer capacidadMax, String estado) {
        this.idSala = idSala;
        this.nombre = nombre;
        this.capacidadMin = capacidadMin;
        this.capacidadMax = capacidadMax;
        this.estado = estado;
    }

    public Long getIdSala() {
        return idSala;
    }

    public void setIdSala(Long idSala) {
        this.idSala = idSala;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCapacidadMin() {
        return capacidadMin;
    }

    public void setCapacidadMin(Integer capacidadMin) {
        this.capacidadMin = capacidadMin;
    }

    public Integer getCapacidadMax() {
        return capacidadMax;
    }

    public void setCapacidadMax(Integer capacidadMax) {
        this.capacidadMax = capacidadMax;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
