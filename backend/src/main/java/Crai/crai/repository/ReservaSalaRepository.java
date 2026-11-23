package Crai.crai.repository;

import Crai.crai.entity.ReservaSala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservaSalaRepository extends JpaRepository<ReservaSala, Long> {
    List<ReservaSala> findBySalaIdSala(Long idSala);
    long countByFechaReservaAndEstado(LocalDate fecha, String estado);

    @Query("SELECT r FROM ReservaSala r WHERE r.sala.idSala = :idSala " +
            "AND r.idReservaSala <> :excluirId " +
            "AND r.fechaReserva = :fecha " +
            "AND r.estado = 'A' " +
            "AND r.horaInicio < :horaFin " +
            "AND r.horaFin > :horaInicio")
    List<ReservaSala> findConflictos(@Param("idSala") Long idSala,
                                     @Param("excluirId") Long excluirId,
                                     @Param("fecha") LocalDate fecha,
                                     @Param("horaInicio") LocalTime horaInicio,
                                     @Param("horaFin") LocalTime horaFin);

    // Reservas activas en las que el estudiante participa (como responsable o integrante) y que se cruzan con el horario

    @Query("SELECT r FROM ReservaSala r LEFT JOIN r.responsable resp " +
            "WHERE r.fechaReserva = :fecha AND r.estado = 'A' " +
            "AND r.horaInicio < :horaFin AND r.horaFin > :horaInicio " +
            "AND (resp.codigoUni = :codigo OR EXISTS (SELECT i.idIntegrante FROM ReservaIntegrante i " +
            "WHERE i.reservaSala = r AND i.codigoUni = :codigo))")
    List<ReservaSala> findActivasDeEstudiante(@Param("fecha") LocalDate fecha,
                                              @Param("horaInicio") LocalTime horaInicio,
                                              @Param("horaFin") LocalTime horaFin,
                                              @Param("codigo") String codigo);

    // Reservas activas que estan ocupando una sala en este momento

    @Query("SELECT r FROM ReservaSala r WHERE r.fechaReserva = :fecha AND r.estado = 'A' " +
            "AND r.horaInicio <= :hora AND r.horaFin > :hora")
    List<ReservaSala> findEnCurso(@Param("fecha") LocalDate fecha, @Param("hora") LocalTime hora);

    @Query("SELECT s.idSala, s.nombre, r.fechaReserva, r.horaInicio, r.horaFin FROM ReservaSala r JOIN r.sala s " +
            "WHERE r.estado = 'A' AND r.fechaReserva BETWEEN :desde AND :hasta")
    List<Object[]> reservasActivasEntre(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT e.nombreEsc, COUNT(r) FROM ReservaSala r JOIN r.responsable u LEFT JOIN u.escuelaProf e " +
            "WHERE r.estado = 'A' AND r.fechaReserva BETWEEN :desde AND :hasta GROUP BY e.nombreEsc")
    List<Object[]> reservasPorEscuelaResponsable(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
}
