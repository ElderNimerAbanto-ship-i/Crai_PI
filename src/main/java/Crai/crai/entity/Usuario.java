package Crai.crai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario",schema = "CRAI")
public class Usuario {
    @Id
    @Column(name = "ID_PERSONA")
    private Long idPersona;

    @OneToOne
    @MapsId
    @JoinColumn(name = "ID_PERSONA")
    private Persona persona;

    @Column(name = "CODIGO_UNI", length = 20, nullable = false, unique = true)
    private String codigoUni;

    @Column(name = "USERNAME", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "PASSWORD", length = 100)
    private String password;

    @Column(name = "ESTADO_MATRICULA")
    private Integer estadoMatricula;

    @Column(name = "ESTADO_ACCESO")
    private Integer estadoAcceso;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROL", length = 20, nullable = false)
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "ID_ESCUELA_PROF")
    private EscuelaProf escuelaProf;

    public Usuario() {
    }

    public Usuario(Long idPersona, Persona persona, String codigoUni, String username, String password, Integer estadoMatricula, Integer estadoAcceso, Rol rol, EscuelaProf escuelaProf) {
        this.idPersona = idPersona;
        this.persona = persona;
        this.codigoUni = codigoUni;
        this.username = username;
        this.password = password;
        this.estadoMatricula = estadoMatricula;
        this.estadoAcceso = estadoAcceso;
        this.rol = rol;
        this.escuelaProf = escuelaProf;
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(Long idPersona) {
        this.idPersona = idPersona;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public String getCodigoUni() {
        return codigoUni;
    }

    public void setCodigoUni(String codigoUni) {
        this.codigoUni = codigoUni;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getEstadoMatricula() {
        return estadoMatricula;
    }

    public void setEstadoMatricula(Integer estadoMatricula) {
        this.estadoMatricula = estadoMatricula;
    }

    public Integer getEstadoAcceso() {
        return estadoAcceso;
    }

    public void setEstadoAcceso(Integer estadoAcceso) {
        this.estadoAcceso = estadoAcceso;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public EscuelaProf getEscuelaProf() {
        return escuelaProf;
    }

    public void setEscuelaProf(EscuelaProf escuelaProf) {
        this.escuelaProf = escuelaProf;
    }
}
