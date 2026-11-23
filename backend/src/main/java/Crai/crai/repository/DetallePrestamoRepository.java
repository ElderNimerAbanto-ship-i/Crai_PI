package Crai.crai.repository;

import Crai.crai.dto.LibroPrestadoDTO;
import Crai.crai.entity.DetallePrestamo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DetallePrestamoRepository extends JpaRepository<DetallePrestamo, Long> {

    Optional<DetallePrestamo> findByEjemplarIdEjemplarAndPrestamoEstado(Long idEjemplar, String estado);

    List<DetallePrestamo> findByPrestamoIdPrestamo(Long idPrestamo);

    @Query("SELECT new Crai.crai.dto.LibroPrestadoDTO(l.idLibro, l.titulo, COUNT(d)) " +
            "FROM DetallePrestamo d JOIN d.ejemplar e JOIN e.libro l " +
            "GROUP BY l.idLibro, l.titulo " +
            "ORDER BY COUNT(d) DESC")
    List<LibroPrestadoDTO> librosMasPrestados(Pageable pageable);

    @Query("SELECT p.fechaPrestamo FROM DetallePrestamo d JOIN d.prestamo p WHERE p.fechaPrestamo >= :desde")
    List<LocalDateTime> fechasDePrestamo(@Param("desde") LocalDateTime desde);
}
