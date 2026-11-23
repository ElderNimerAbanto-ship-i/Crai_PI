package Crai.crai.repository;

import Crai.crai.entity.ReservaIntegrante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaIntegranteRepository extends JpaRepository<ReservaIntegrante, Long> {
    List<ReservaIntegrante> findByReservaSalaIdReservaSala(Long idReservaSala);
    long countByReservaSalaIdReservaSala(Long idReservaSala);
    boolean existsByReservaSalaIdReservaSalaAndCodigoUni(Long idReservaSala, String codigoUni);
    void deleteByReservaSalaIdReservaSala(Long idReservaSala);

    @Query("SELECT e.nombreEsc, COUNT(i) FROM ReservaIntegrante i JOIN i.reservaSala r " +
            "JOIN Usuario u ON u.codigoUni = i.codigoUni LEFT JOIN u.escuelaProf e " +
            "WHERE r.estado = 'A' AND r.fechaReserva BETWEEN :desde AND :hasta GROUP BY e.nombreEsc")
    List<Object[]> integrantesPorEscuela(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
}
