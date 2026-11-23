package Crai.crai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ejemplar",schema = "CRAI")
public class Ejemplar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEjemplar;

    @Column(name = "ESTADO", length = 30, nullable = false)
    private String estado;

    @Column(name = "ISBN", length = 30)
    private String isbn;

    @ManyToOne
    @JoinColumn(name = "ID_LIBRO", nullable = false)
    private Libro libro;

    public Ejemplar() {
    }

    public Ejemplar(Long idEjemplar, String estado, String isbn, Libro libro) {
        this.idEjemplar = idEjemplar;
        this.estado = estado;
        this.isbn = isbn;
        this.libro = libro;
    }

    public Long getIdEjemplar() {
        return idEjemplar;
    }

    public void setIdEjemplar(Long idEjemplar) {
        this.idEjemplar = idEjemplar;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }
}
