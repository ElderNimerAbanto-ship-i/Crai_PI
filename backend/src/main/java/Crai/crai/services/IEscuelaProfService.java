package Crai.crai.services;

import Crai.crai.dto.EscuelaProfDTO;

import java.util.List;
import java.util.Optional;

public interface IEscuelaProfService {
    List<EscuelaProfDTO> listarTodo();
    Optional<EscuelaProfDTO> buscarPorId(Long id);
    EscuelaProfDTO crear(EscuelaProfDTO dto);
    Optional<EscuelaProfDTO> actualizar(Long id, EscuelaProfDTO dto);
    boolean eliminar(Long id);
}
