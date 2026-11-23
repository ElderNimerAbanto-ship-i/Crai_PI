package Crai.crai.services;

import Crai.crai.dto.ControlIngresoDTO;

import java.util.List;
import java.util.Optional;

public interface IControlIngresoService {
    List<ControlIngresoDTO> listarTodo();
    List<ControlIngresoDTO> listarPorPersona(Long idPersona);
    Optional<ControlIngresoDTO> buscarPorId(Long id);
    ControlIngresoDTO registrar(ControlIngresoDTO dto);
}
