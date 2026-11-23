package Crai.crai.repository;

import Crai.crai.entity.Ejemplar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjemplarRepository extends JpaRepository<Ejemplar, Long> {
    List<Ejemplar> findByLibroIdLibro(Long idLibro);
    List<Ejemplar> findByEstado(String estado);
    long countByEstado(String estado);
    long countByLibroIdLibroAndEstado(Long idLibro, String estado);
    boolean existsByLibroIdLibro(Long idLibro);
}
