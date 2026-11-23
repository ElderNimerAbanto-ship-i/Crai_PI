package Crai.crai.repository;

import Crai.crai.entity.Prestamo;
import Crai.crai.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    List<Prestamo> findByPersonaIdPersona(Long idPersona);
    List<Prestamo> findByEstado(String estado);
    List<Prestamo> findByEstadoAndFechaVencimientoBefore(String estado, LocalDateTime fecha);
    long countByEstado(String estado);
    long countByEstadoAndFechaVencimientoBefore(String estado, LocalDateTime fecha);
    long countByPersonaIdPersonaAndEstado(Long idPersona, String estado);
    boolean existsByPersonaIdPersonaAndEstadoAndFechaVencimientoBefore(Long idPersona, String estado, LocalDateTime fecha);

    @Query("SELECT e.nombreEsc, COUNT(p) FROM Prestamo p " +
            "JOIN Usuario u ON u.idPersona = p.persona.idPersona LEFT JOIN u.escuelaProf e " +
            "WHERE p.fechaPrestamo >= :desde AND p.fechaPrestamo < :hasta AND u.rol <> :excluido " +
            "GROUP BY e.nombreEsc")
    List<Object[]> prestamosPorEscuela(@Param("desde") LocalDateTime desde,
                                       @Param("hasta") LocalDateTime hasta,
                                       @Param("excluido") Rol excluido);
}
