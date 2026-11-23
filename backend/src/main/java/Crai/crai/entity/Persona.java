package Crai.crai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "persona",schema = "CRAI")
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPersona;

    @Column(name = "NOMBRES", length = 100, nullable = false)
    private String nombres;

    @Column(name = "APELLIDOS", length = 100, nullable = false)
    private String apellidos;

    @Column(name = "EMAIL", length = 150, unique = true)
    private String email;

    public Persona() {
    }

    public Persona(Long idPersona, String nombres, String apellidos, String email) {
        this.idPersona = idPersona;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
