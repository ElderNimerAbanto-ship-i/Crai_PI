package Crai.crai.services;

import Crai.crai.dto.UsuarioDTO;
import Crai.crai.dto.UsuarioRegistroDTO;
import Crai.crai.entity.EscuelaProf;
import Crai.crai.entity.Persona;
import Crai.crai.entity.Rol;
import Crai.crai.entity.Usuario;
import Crai.crai.exception.DuplicateResourceException;
import Crai.crai.exception.ResourceNotFoundException;
import Crai.crai.repository.EscuelaProfRepository;
import Crai.crai.repository.PersonaRepository;
import Crai.crai.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {
    private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EscuelaProfRepository escuelaProfRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(PersonaRepository personaRepository, UsuarioRepository usuarioRepository,
                              EscuelaProfRepository escuelaProfRepository, PasswordEncoder passwordEncoder) {
        this.personaRepository = personaRepository;
        this.usuarioRepository = usuarioRepository;
        this.escuelaProfRepository = escuelaProfRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UsuarioDTO registrar(UsuarioRegistroDTO dto) {
        // validar que email, usuario y codigo universitario sean unicos
        if (personaRepository.existsByEmail(dto.email())) {
            throw new DuplicateResourceException("El email '" + dto.email() + "' ya esta registrado");
        }
        if (usuarioRepository.existsByUsername(dto.username())) {
            throw new DuplicateResourceException("El nombre de usuario '" + dto.username() + "' ya existe");
        }
        if (usuarioRepository.existsByCodigoUni(dto.codigoUni())) {
            throw new DuplicateResourceException("El codigo universitario '" + dto.codigoUni() + "' ya esta registrado");
        }
        EscuelaProf escuela = null;
        if (dto.idEscuelaProf() != null) {
            escuela = escuelaProfRepository.findById(dto.idEscuelaProf())
                    .orElseThrow(() -> new ResourceNotFoundException("Escuela no encontrada con id:" + dto.idEscuelaProf()));
        }

        Persona p = new Persona();
        p.setNombres(dto.nombres());
        p.setApellidos(dto.apellidos());
        p.setEmail(dto.email());
        Persona personaGuardada = personaRepository.save(p);

        Usuario u = new Usuario();
        u.setPersona(personaGuardada);
        u.setCodigoUni(dto.codigoUni());
        u.setUsername(dto.username());
        String clave = (dto.password() == null || dto.password().isBlank()) ? dto.codigoUni() : dto.password();
        u.setPassword(passwordEncoder.encode(dto.password()));
        u.setEstadoMatricula(1);
        u.setEstadoAcceso(1);
        u.setRol(Rol.ESTUDIANTE);
        u.setEscuelaProf(escuela);

        return convertToDTO(usuarioRepository.save(u));
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
