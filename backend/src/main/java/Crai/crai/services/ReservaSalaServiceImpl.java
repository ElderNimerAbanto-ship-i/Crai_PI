package Crai.crai.services;

import Crai.crai.dto.ReservaSalaDTO;
import Crai.crai.entity.ReservaSala;
import Crai.crai.entity.Sala;
import Crai.crai.exception.BusinessRuleException;
import Crai.crai.exception.ResourceNotFoundException;
import Crai.crai.repository.ReservaIntegranteRepository;
import Crai.crai.repository.ReservaSalaRepository;
import Crai.crai.repository.SalaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaSalaServiceImpl implements IReservaSalaService {
    private static final String ACTIVA = "A";
    private static final String CANCELADA = "C";

    private final ReservaSalaRepository reservaSalaRepository;
    private final SalaRepository salaRepository;
    private final ReservaIntegranteRepository reservaIntegranteRepository;

    public ReservaSalaServiceImpl(ReservaSalaRepository reservaSalaRepository, SalaRepository salaRepository,
                                  ReservaIntegranteRepository reservaIntegranteRepository) {
        this.reservaSalaRepository = reservaSalaRepository;
        this.salaRepository = salaRepository;
        this.reservaIntegranteRepository = reservaIntegranteRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaSalaDTO> listarTodo() {
        return reservaSalaRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReservaSalaDTO> buscarPorId(Long id) {
        return reservaSalaRepository.findById(id).map(this::convertToDTO);
    }




































    @Override
    @Transactional
    public ReservaSalaDTO crear(ReservaSalaDTO dto) {
        validarHorario(dto);
        if (dto.fechaReserva().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se puede reservar en una fecha pasada");
        }
        Sala s = buscarSalaDisponible(dto.idSala());
        validarSinCruces(0L, dto);

        ReservaSala r = new ReservaSala();
        r.setFechaReserva(dto.fechaReserva());
        r.setHoraInicio(dto.horaInicio());
        r.setHoraFin(dto.horaFin());
        r.setEstado(ACTIVA);
        r.setSala(s);
        return convertToDTO(reservaSalaRepository.save(r));
    }

    @Override
    @Transactional
    public Optional<ReservaSalaDTO> actualizar(Long id, ReservaSalaDTO dto) {
        validarHorario(dto);
        return reservaSalaRepository.findById(id).map(r -> {
            if (CANCELADA.equals(r.getEstado())) {
                throw new BusinessRuleException("No se puede modificar una reserva cancelada");
            }
            if (!dto.fechaReserva().equals(r.getFechaReserva()) && dto.fechaReserva().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("No se puede reservar en una fecha pasada");
            }
            Sala s = buscarSalaDisponible(dto.idSala());
            long integrantes = reservaIntegranteRepository.countByReservaSalaIdReservaSala(id)
                    + (r.getResponsable() != null ? 1 : 0);
            if (integrantes > s.getCapacidadMax()) {
                throw new BusinessRuleException("La reserva tiene mas integrantes que la capacidad de la sala");
            }
            validarSinCruces(id, dto);

            r.setFechaReserva(dto.fechaReserva());
            r.setHoraInicio(dto.horaInicio());
            r.setHoraFin(dto.horaFin());
            r.setSala(s);
            return convertToDTO(reservaSalaRepository.save(r));
        });
    }

    @Override
    @Transactional
    public Optional<ReservaSalaDTO> cancelar(Long id) {
        return reservaSalaRepository.findById(id).map(r -> {
            if (CANCELADA.equals(r.getEstado())) {
                throw new BusinessRuleException("La reserva ya esta cancelada");
            }
            r.setEstado(CANCELADA);
            return convertToDTO(reservaSalaRepository.save(r));
        });
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {
        if (reservaSalaRepository.existsById(id)) {
            reservaIntegranteRepository.deleteByReservaSalaIdReservaSala(id);
            reservaSalaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void validarHorario(ReservaSalaDTO dto) {
        if (!dto.horaFin().isAfter(dto.horaInicio())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }
    }

    private Sala buscarSalaDisponible(Long idSala) {
        Sala s = salaRepository.findById(idSala)
                .orElseThrow(() -> new ResourceNotFoundException("Sala no encontrada con id:" + idSala));
        if (!"DISPONIBLE".equals(s.getEstado())) {
            throw new BusinessRuleException("La sala '" + s.getNombre() + "' no esta disponible");
        }
        return s;
    }

    // excluirId = id de la propia reserva al actualizar, 0 al crear
    private void validarSinCruces(Long excluirId, ReservaSalaDTO dto) {
        List<ReservaSala> conflictos = reservaSalaRepository.findConflictos(
                dto.idSala(), excluirId, dto.fechaReserva(), dto.horaInicio(), dto.horaFin());
        if (!conflictos.isEmpty()) {
            throw new BusinessRuleException("La sala ya tiene una reserva activa que se cruza con ese horario");
        }
    }

    private ReservaSalaDTO convertToDTO(ReservaSala r) {
        return new ReservaSalaDTO(
                r.getIdReservaSala(),
                r.getFechaReserva(),
                r.getHoraInicio(),
                r.getHoraFin(),
                r.getEstado(),
                r.getSala().getIdSala(),
                r.getSala().getNombre(),
                reservaIntegranteRepository.countByReservaSalaIdReservaSala(r.getIdReservaSala()),
                r.getResponsable() != null ? r.getResponsable().getCodigoUni() : null,
                r.getResponsable() != null
                        ? r.getResponsable().getPersona().getNombres() + " " + r.getResponsable().getPersona().getApellidos()
                        : null,
                r.getActividad() != null ? r.getActividad().name() : null,
                r.getDuracionMinutos()
        );
    }
}
