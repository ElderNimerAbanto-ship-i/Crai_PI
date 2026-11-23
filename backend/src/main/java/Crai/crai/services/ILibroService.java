package Crai.crai.services;

import Crai.crai.dto.LibroDTO;

import java.util.List;
import java.util.Optional;

public interface ILibroService {
    List<LibroDTO> listarTodo(String buscar);
    Optional<LibroDTO> buscarPorId(Long id);
    LibroDTO crear(LibroDTO dto);
    Optional<LibroDTO> actualizar(Long id, LibroDTO dto);
    boolean eliminar(Long id);
}
