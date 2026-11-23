package Crai.crai.repository;

import Crai.crai.dto.LibroPrestadoDTO;
import Crai.crai.entity.LibroMasPrestado;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LibroMasPrestadoRepository extends JpaRepository<LibroMasPrestado, Long> {
    // Opción usando JPQL proyectando directamente al record DTO de Java
    @Query("SELECT new Crai.crai.dto.LibroPrestadoDTO(v.idLibro, v.titulo, v.totalPrestamos) " +
            "FROM LibroMasPrestado v ORDER BY v.totalPrestamos DESC")
    List<LibroPrestadoDTO> obtenerLibrosMasPrestados(Pageable pageable);
}
