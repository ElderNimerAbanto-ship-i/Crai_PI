package Crai.crai.services;

import Crai.crai.dto.SalaDTO;

import java.util.List;
import java.util.Optional;

public interface ISalaService {
    List<SalaDTO> listarTodo();
    Optional<SalaDTO> buscarPorId(Long id);
    SalaDTO crear(SalaDTO dto);
    Optional<SalaDTO> actualizar(Long id, SalaDTO dto);
    boolean eliminar(Long id);
}
