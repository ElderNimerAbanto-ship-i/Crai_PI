package Crai.crai.repository;

import Crai.crai.entity.Sala;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {
    boolean existsByNombreIgnoreCase(String nombre);

    // Salas disponibles cuya capacidad admite al grupo, de menor a mayor capacidad
    @Query("SELECT s FROM Sala s WHERE s.estado = 'DISPONIBLE' " +
            "AND s.capacidadMin <= :personas AND s.capacidadMax >= :personas " +
            "ORDER BY s.capacidadMax, s.nombre")
    List<Sala> findCandidatas(@Param("personas") int personas);

    // Igual que la anterior pero bloqueando las filas: dos grupos no pueden quedarse con la misma sala a la vez
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Sala s WHERE s.estado = 'DISPONIBLE' " +
            "AND s.capacidadMin <= :personas AND s.capacidadMax >= :personas " +
            "ORDER BY s.capacidadMax, s.nombre")
    List<Sala> findCandidatasParaReservar(@Param("personas") int personas);


    // Una sala concreta bloqueando la fila: evita que dos grupos reserven el mismo horario a la vez
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Sala s WHERE s.idSala = :idSala")
    Optional<Sala> findByIdParaReservar(@Param("idSala") Long idSala);
}
