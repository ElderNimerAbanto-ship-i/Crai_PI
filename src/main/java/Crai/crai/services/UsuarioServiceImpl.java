package Crai.crai.services;

import Crai.crai.dto.UsuarioDTO;
import Crai.crai.entity.Usuario;
import Crai.crai.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodo() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioDTO> buscarPorId(Long id) {
        return usuarioRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional
    public Optional<UsuarioDTO> cambiarAcceso(Long id, boolean habilitado) {
        return usuarioRepository.findById(id).map(u -> {
            u.setEstadoAcceso(habilitado ? 1 : 0);
            return convertToDTO(usuarioRepository.save(u));
        });
    }

    private UsuarioDTO convertToDTO(Usuario u) {
        return new UsuarioDTO(
                u.getIdPersona(),
                u.getUsername(),
                u.getRol().name(),
                u.getCodigoUni(),
                u.getPersona().getNombres() + " " + u.getPersona().getApellidos(),
                u.getPersona().getEmail(),
                u.getEstadoAcceso() != null && u.getEstadoAcceso() == 1,
                u.getEscuelaProf() != null ? u.getEscuelaProf().getNombreEsc() : null
        );
    }
}
