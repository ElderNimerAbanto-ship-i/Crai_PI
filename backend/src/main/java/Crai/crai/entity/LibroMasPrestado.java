package Crai.crai.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "vw_libros_mas_prestados",schema = "CRAI")
public class LibroMasPrestado {
    @Id
    @Column(name = "id_libro")
    private Long idLibro;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "total_prestamos")
    private Long totalPrestamos;

    @Column(name = "posicion")
    private Long posicion;

    public LibroMasPrestado() {
    }

    public LibroMasPrestado(Long idLibro, String titulo, Long totalPrestamos, Long posicion) {
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.totalPrestamos = totalPrestamos;
        this.posicion = posicion;
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

    public Long getTotalPrestamos() {
        return totalPrestamos;
    }

    public void setTotalPrestamos(Long totalPrestamos) {
        this.totalPrestamos = totalPrestamos;
    }

    public Long getPosicion() {
        return posicion;
    }

    public void setPosicion(Long posicion) {
        this.posicion = posicion;
    }
}
