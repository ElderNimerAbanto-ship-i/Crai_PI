package Crai.crai.services;

import Crai.crai.dto.UsuarioDTO;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    List<UsuarioDTO> listarTodo();
    Optional<UsuarioDTO> buscarPorId(Long id);
    Optional<UsuarioDTO> cambiarAcceso(Long id, boolean habilitado);
}
