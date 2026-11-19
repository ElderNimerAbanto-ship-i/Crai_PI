package Crai.crai.services;

import Crai.crai.dto.ReservaSalaDTO;

import java.util.List;
import java.util.Optional;

public interface IReservaSalaService {
    List<ReservaSalaDTO> listarTodo();
    Optional<ReservaSalaDTO> buscarPorId(Long id);
    ReservaSalaDTO crear(ReservaSalaDTO dto);
    Optional<ReservaSalaDTO> actualizar(Long id, ReservaSalaDTO dto);
    Optional<ReservaSalaDTO> cancelar(Long id);
    boolean eliminar(Long id);
}
