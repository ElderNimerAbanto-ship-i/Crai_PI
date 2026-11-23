package Crai.crai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categoria",schema = "CRAI")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategoria;

    @Column(name = "NOMBRE", length = 150, nullable = false, unique = true)
    private String nombre;

    public Categoria() {
    }

    public Categoria(Long idCategoria, String nombre) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
