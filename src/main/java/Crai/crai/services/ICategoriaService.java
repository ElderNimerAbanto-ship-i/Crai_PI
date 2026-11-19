package Crai.crai.services;

import Crai.crai.dto.CategoriaDTO;

import java.util.List;
import java.util.Optional;

public interface ICategoriaService {
    List<CategoriaDTO> listarTodo();
    Optional<CategoriaDTO> buscarPorId(Long id);
    CategoriaDTO crear(CategoriaDTO dto);
    Optional<CategoriaDTO> actualizar(Long id, CategoriaDTO dto);
    boolean eliminar(Long id);
}
