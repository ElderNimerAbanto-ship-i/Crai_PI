package Crai.crai.services;

import Crai.crai.dto.ReservaIntegranteDTO;
import Crai.crai.entity.ReservaIntegrante;
import Crai.crai.entity.ReservaSala;
import Crai.crai.entity.Usuario;
import Crai.crai.exception.BusinessRuleException;
import Crai.crai.exception.DuplicateResourceException;
import Crai.crai.exception.ResourceNotFoundException;
import Crai.crai.repository.ReservaIntegranteRepository;
import Crai.crai.repository.ReservaSalaRepository;
import Crai.crai.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaIntegranteServiceImpl implements IReservaIntegranteService {
    private final ReservaIntegranteRepository reservaIntegranteRepository;
    private final ReservaSalaRepository reservaSalaRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaIntegranteServiceImpl(ReservaIntegranteRepository reservaIntegranteRepository,
                                        ReservaSalaRepository reservaSalaRepository,
                                        UsuarioRepository usuarioRepository) {
        this.reservaIntegranteRepository = reservaIntegranteRepository;
        this.reservaSalaRepository = reservaSalaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaIntegranteDTO> listarTodo() {
        return reservaIntegranteRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaIntegranteDTO> listarPorReserva(Long idReservaSala) {
        return reservaIntegranteRepository.findByReservaSalaIdReservaSala(idReservaSala).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReservaIntegranteDTO> buscarPorId(Long id) {
        return reservaIntegranteRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional
    public ReservaIntegranteDTO crear(ReservaIntegranteDTO dto) {
        ReservaSala rs = validarReservaYUsuario(dto);
        validarNoDuplicado(dto);
        validarCapacidad(rs);

        ReservaIntegrante ri = new ReservaIntegrante();
        ri.setCodigoUni(dto.codigoUni());
        ri.setReservaSala(rs);
        return convertToDTO(reservaIntegranteRepository.save(ri));
    }

    @Override
    @Transactional
    public Optional<ReservaIntegranteDTO> actualizar(Long id, ReservaIntegranteDTO dto) {
        return reservaIntegranteRepository.findById(id).map(ri -> {
            ReservaSala rs = validarReservaYUsuario(dto);
            boolean cambioReserva = !rs.getIdReservaSala().equals(ri.getReservaSala().getIdReservaSala());
            boolean cambioCodigo = !dto.codigoUni().equals(ri.getCodigoUni());
            if (cambioReserva || cambioCodigo) {
                validarNoDuplicado(dto);
            }
            if (cambioReserva) {
                validarCapacidad(rs);
            }
            ri.setCodigoUni(dto.codigoUni());
            ri.setReservaSala(rs);
            return convertToDTO(reservaIntegranteRepository.save(ri));
        });
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {
        if (reservaIntegranteRepository.existsById(id)) {
            reservaIntegranteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private ReservaSala validarReservaYUsuario(ReservaIntegranteDTO dto) {
        ReservaSala rs = reservaSalaRepository.findById(dto.idReservaSala())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva de sala no encontrada con id:" + dto.idReservaSala()));
        if (!"A".equals(rs.getEstado())) {
            throw new BusinessRuleException("La reserva esta cancelada, no admite integrantes");
        }
        if (!usuarioRepository.existsByCodigoUni(dto.codigoUni())) {
            throw new ResourceNotFoundException("Usuario no encontrado con codigo:" + dto.codigoUni());
        }
        return rs;
    }

    private void validarNoDuplicado(ReservaIntegranteDTO dto) {
        if (reservaIntegranteRepository.existsByReservaSalaIdReservaSalaAndCodigoUni(dto.idReservaSala(), dto.codigoUni())) {
            throw new DuplicateResourceException("El usuario " + dto.codigoUni() + " ya es integrante de esta reserva");
        }
    }

    private void validarCapacidad(ReservaSala rs) {
        // el responsable tambien ocupa un lugar en la sala
        long actuales = reservaIntegranteRepository.countByReservaSalaIdReservaSala(rs.getIdReservaSala())
                + (rs.getResponsable() != null ? 1 : 0);
        if (actuales >= rs.getSala().getCapacidadMax()) {
            throw new BusinessRuleException("La sala '" + rs.getSala().getNombre() + "' ya alcanzo su capacidad maxima de "
                    + rs.getSala().getCapacidadMax() + " personas");
        }
    }

    private ReservaIntegranteDTO convertToDTO(ReservaIntegrante ri) {
        String nombre = usuarioRepository.findByCodigoUni(ri.getCodigoUni())
                .map((Usuario u) -> u.getPersona().getNombres() + " " + u.getPersona().getApellidos())
                .orElse(null);
        return new ReservaIntegranteDTO(
                ri.getIdIntegrante(),
                ri.getCodigoUni(),
                ri.getReservaSala().getIdReservaSala(),
                nombre
        );
    }
}
