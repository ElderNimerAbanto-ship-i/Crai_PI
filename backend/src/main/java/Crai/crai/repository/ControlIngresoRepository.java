package Crai.crai.repository;

import Crai.crai.entity.ControlIngreso;
import Crai.crai.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ControlIngresoRepository extends JpaRepository<ControlIngreso, Long> {
    List<ControlIngreso> findByUsuarioIdPersona(Long idPersona);
    Optional<ControlIngreso> findTopByUsuarioIdPersonaOrderByFechaHoraDesc(Long idPersona);
    long countByFechaHoraBetween(LocalDateTime desde, LocalDateTime hasta);
    long countByTipoAccesoAndFechaHoraBetween(String tipoAcceso, LocalDateTime desde, LocalDateTime hasta);
    @Query("SELECT e.nombreEsc, COUNT(c) FROM ControlIngreso c JOIN c.usuario u LEFT JOIN u.escuelaProf e " +
            "WHERE c.tipoAcceso = 'ENTRADA' AND c.fechaHora >= :desde AND c.fechaHora < :hasta " +
            "AND u.rol <> :excluido GROUP BY e.nombreEsc")
    List<Object[]> ingresosPorEscuela(@Param("desde") LocalDateTime desde,
                                      @Param("hasta") LocalDateTime hasta,
                                      @Param("excluido") Rol excluido);
}
