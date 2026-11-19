package Crai.crai.services;

import Crai.crai.dto.EjemplarDTO;

import java.util.List;
import java.util.Optional;

public interface IEjemplarService {
    List<EjemplarDTO> listarTodo();
    Optional<EjemplarDTO> buscarPorId(Long id);
    EjemplarDTO crear(EjemplarDTO dto);
    Optional<EjemplarDTO> actualizar(Long id, EjemplarDTO dto);
    boolean eliminar(Long id);
}
