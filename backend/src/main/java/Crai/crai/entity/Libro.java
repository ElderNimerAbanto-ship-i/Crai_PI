package Crai.crai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "libro",schema = "CRAI")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLibro;

    @Column(name = "TITULO", length = 200, nullable = false)
    private String titulo;

    @Column(name = "AUTOR", length = 150, nullable = false)
    private String autor;

    @Column(name = "SECCION_ESPECIAL", length = 100)
    private String seccionEspecial;

    @Column(name = "IMAGEN", length = 300)
    private String imagen;

    @ManyToOne
    @JoinColumn(name = "ID_CATEGORIA", nullable = false)
    private Categoria categoria;

    public Libro() {
    }

    public Libro(Long idLibro, String titulo, String autor, String seccionEspecial, String imagen, Categoria categoria) {
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.autor = autor;
        this.seccionEspecial = seccionEspecial;
        this.imagen = imagen;
        this.categoria = categoria;
    }

    public Long getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(Long idLibro) {
        this.idLibro = idLibro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getSeccionEspecial() {
        return seccionEspecial;
    }

    public void setSeccionEspecial(String seccionEspecial) {
        this.seccionEspecial = seccionEspecial;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}
