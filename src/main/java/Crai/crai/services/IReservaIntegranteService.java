package Crai.crai.services;

import Crai.crai.dto.ReservaIntegranteDTO;

import java.util.List;
import java.util.Optional;

public interface IReservaIntegranteService {
    List<ReservaIntegranteDTO> listarTodo();
    List<ReservaIntegranteDTO> listarPorReserva(Long idReservaSala);
    Optional<ReservaIntegranteDTO> buscarPorId(Long id);
    ReservaIntegranteDTO crear(ReservaIntegranteDTO dto);
    Optional<ReservaIntegranteDTO> actualizar(Long id, ReservaIntegranteDTO dto);
    boolean eliminar(Long id);
}
