package Crai.crai.services;

import Crai.crai.dto.ControlIngresoDTO;
import Crai.crai.entity.ControlIngreso;
import Crai.crai.entity.Usuario;
import Crai.crai.exception.BusinessRuleException;
import Crai.crai.exception.ResourceNotFoundException;
import Crai.crai.repository.ControlIngresoRepository;
import Crai.crai.repository.UsuarioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ControlIngresoServiceImpl implements IControlIngresoService {

    private final ControlIngresoRepository controlIngresoRepository;
    private final UsuarioRepository usuarioRepository;

    public ControlIngresoServiceImpl(ControlIngresoRepository controlIngresoRepository, UsuarioRepository usuarioRepository) {
        this.controlIngresoRepository = controlIngresoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public ControlIngresoDTO registrar(ControlIngresoDTO dto) {
        Usuario u = usuarioRepository.findByCodigoUni(dto.codigoUni().trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro ningun usuario con el codigo universitario: " + dto.codigoUni()));

        if (u.getEstadoAcceso() == null || u.getEstadoAcceso() != 1) {
            throw new BusinessRuleException("Acceso denegado: el usuario no tiene acceso habilitado");
        }

        // Ultimo movimiento de HOY: si es ENTRADA, la persona esta dentro del CRAI
        boolean estaDentro = controlIngresoRepository
                .findTopByUsuarioIdPersonaOrderByFechaHoraDesc(u.getIdPersona())
                .filter(c -> c.getFechaHora().toLocalDate().equals(LocalDate.now()))
                .map(c -> "ENTRADA".equals(c.getTipoAcceso()))
                .orElse(false);

        // Se valida ANTES del INSERT para no depender del error ORA-20020 de trg_ingreso_alterna
        if ("ENTRADA".equals(dto.tipoAcceso()) && estaDentro) {
            throw new BusinessRuleException("El usuario ya se encuentra registrado");
        }
        if ("SALIDA".equals(dto.tipoAcceso()) && !estaDentro) {
            throw new BusinessRuleException("El usuario no tiene una entrada registrada hoy");
        }

        ControlIngreso ingreso = new ControlIngreso();
        ingreso.setUsuario(u);
        ingreso.setTipoAcceso(dto.tipoAcceso());
        ingreso.setFechaHora(LocalDateTime.now());

        return convertToDTO(controlIngresoRepository.saveAndFlush(ingreso));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ControlIngresoDTO> listarTodo() {
        return controlIngresoRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaHora")).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ControlIngresoDTO> listarPorPersona(Long idPersona) {
        return controlIngresoRepository.findByUsuarioIdPersona(idPersona).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ControlIngresoDTO> buscarPorId(Long id) {
        return controlIngresoRepository.findById(id)
                .map(this::convertToDTO);
    }

    private ControlIngresoDTO convertToDTO(ControlIngreso c) {
        Usuario u = c.getUsuario();
        return new ControlIngresoDTO(
                c.getIdIngreso(),
                c.getFechaHora(),
                c.getTipoAcceso(),
                u.getCodigoUni(),
                u.getPersona().getNombres() + " " + u.getPersona().getApellidos(),
                // un administrador puede no tener escuela profesional
                u.getEscuelaProf() != null ? u.getEscuelaProf().getNombreEsc() : null
        );
    }
}