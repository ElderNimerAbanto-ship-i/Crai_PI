package Crai.crai.services;

import Crai.crai.dto.*;
import Crai.crai.entity.*;
import Crai.crai.exception.BusinessRuleException;
import Crai.crai.exception.ResourceNotFoundException;
import Crai.crai.repository.ReservaIntegranteRepository;
import Crai.crai.repository.ReservaSalaRepository;
import Crai.crai.repository.SalaRepository;
import Crai.crai.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReservaPublicaServiceImpl implements IReservaPublicaService {
    // Reglas de la reserva de salas (ajustables)
    private static final List<Integer> DURACIONES = List.of(60, 90, 120);
    private static final int MIN_PERSONAS = 3;
    private static final int MAX_PERSONAS = 8;
    private static final int MARGEN_MINUTOS = 5;
    private static final LocalTime APERTURA = LocalTime.of(8, 0);
    private static final LocalTime CIERRE = LocalTime.of(21, 0);

    private final UsuarioRepository usuarioRepository;
    private final SalaRepository salaRepository;
    private final ReservaSalaRepository reservaSalaRepository;
    private final ReservaIntegranteRepository reservaIntegranteRepository;

    public ReservaPublicaServiceImpl(UsuarioRepository usuarioRepository, SalaRepository salaRepository,
                                     ReservaSalaRepository reservaSalaRepository,
                                     ReservaIntegranteRepository reservaIntegranteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.salaRepository = salaRepository;
        this.reservaSalaRepository = reservaSalaRepository;
        this.reservaIntegranteRepository = reservaIntegranteRepository;
    }

    // Fecha y horario de la reserva: siempre hoy
    private record Ventana(LocalDate fecha, LocalTime inicio, LocalTime fin) {
    }

    @Override
    @Transactional(readOnly = true)
    public EstudianteValidadoDTO validarEstudiante(String codigoUni) {
        return convertToDTO(obtenerEstudianteHabilitado(codigoUni));
    }

    @Override
    @Transactional(readOnly = true)
    public DisponibilidadDTO consultarDisponibilidad(int duracionMinutos, int cantidadPersonas) {
        validarTamanoGrupo(cantidadPersonas);
        Ventana v = calcularVentana(duracionMinutos);
        Optional<Sala> sala = buscarSalaLibre(salaRepository.findCandidatas(cantidadPersonas), v, MARGEN_MINUTOS);
        if (sala.isEmpty()) {
            return new DisponibilidadDTO(false, null, null, v.inicio(), v.fin(),
                    "No hay una sala libre para " + cantidadPersonas + " personas durante " + duracionMinutos
                            + " minutos. Prueba con menos tiempo o consulta mas tarde");
        }
        Sala s = sala.get();
        return new DisponibilidadDTO(true, s.getIdSala(), s.getNombre(), v.inicio(), v.fin(),
                "Sala disponible de " + v.inicio() + " a " + v.fin() + ". Tendran " + MARGEN_MINUTOS
                        + " minutos para ingresar y acomodarse");
    }

    @Override
    @Transactional
    public ReservaPublicaResultadoDTO reservar(ReservaPublicaDTO dto) {
        List<String> codigos = new ArrayList<>();
        codigos.add(dto.codigoResponsable().trim());
        dto.codigosIntegrantes().forEach(c -> codigos.add(c.trim()));
        if (new HashSet<>(codigos).size() != codigos.size()) {
            throw new IllegalArgumentException("Hay codigos universitarios repetidos en el grupo");
        }
        validarTamanoGrupo(codigos.size());
        boolean elegido = dto.idSala() != null;
        Ventana v;
        if (elegido) {
            if (dto.horaInicio() == null) {
                throw new IllegalArgumentException("Elige el horario de la sala");
            }
            v = calcularVentanaElegida(dto.horaInicio(), dto.duracionMinutos());
        } else {
            v = calcularVentana(dto.duracionMinutos());
        }


        List<Sala> candidatas = elegido
                ? salaRepository.findByIdParaReservar(dto.idSala())
                .filter(s -> "DISPONIBLE".equals(s.getEstado())
                        && s.getCapacidadMin() <= codigos.size()
                        && s.getCapacidadMax() >= codigos.size())
                .stream().toList()
                : salaRepository.findCandidatasParaReservar(codigos.size());
        if (elegido && candidatas.isEmpty()) {
            throw new BusinessRuleException("La sala elegida no esta disponible o no admite a " + codigos.size() + " personas");
        }


        List<Usuario> estudiantes = new ArrayList<>();
        for (String codigo : codigos) {
            Usuario u = obtenerEstudianteHabilitado(codigo);
            verificarSinReservaActiva(u.getCodigoUni(), v.fecha(), v.inicio(), v.fin());
            estudiantes.add(u);
        }


        Sala sala = buscarSalaLibre(candidatas, v, elegido ? 0 : MARGEN_MINUTOS)
                .orElseThrow(() -> new BusinessRuleException(elegido
                        ? "Ese horario ya fue reservado por otro grupo. Elige otro"
                        : "No hay una sala libre para " + codigos.size() + " personas durante "
                        + dto.duracionMinutos() + " minutos"));


        Usuario responsable = estudiantes.get(0);
        ReservaSala r = new ReservaSala();
        r.setFechaReserva(v.fecha());
        r.setHoraInicio(v.inicio());
        r.setHoraFin(v.fin());
        r.setEstado("A");
        r.setSala(sala);
        r.setResponsable(responsable);
        r.setActividad(dto.actividad());
        r.setDuracionMinutos(dto.duracionMinutos());
        r.setFechaRegistro(LocalDateTime.now());
        ReservaSala guardada = reservaSalaRepository.save(r);

        List<EstudianteValidadoDTO> integrantes = new ArrayList<>();
        for (Usuario u : estudiantes.subList(1, estudiantes.size())) {
            ReservaIntegrante ri = new ReservaIntegrante();
            ri.setCodigoUni(u.getCodigoUni());
            ri.setReservaSala(guardada);
            reservaIntegranteRepository.save(ri);
            integrantes.add(convertToDTO(u));
        }

        return new ReservaPublicaResultadoDTO(
                guardada.getIdReservaSala(),
                sala.getNombre(),
                v.fecha(),
                v.inicio(),
                v.fin(),
                dto.duracionMinutos(),
                dto.actividad().name(),
                convertToDTO(responsable),
                integrantes,
                "Reserva registrada en la sala " + sala.getNombre() + ". Tienen hasta las "
                        + v.inicio().plusMinutes(MARGEN_MINUTOS) + " para ingresar"
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaEstadoDTO> estadoSalas() {
        // reservas que estan ocupando una sala en este momento, por sala
        Map<Long, ReservaSala> enCurso = reservaSalaRepository.findEnCurso(LocalDate.now(), horaActual()).stream()
                .collect(Collectors.toMap(r -> r.getSala().getIdSala(), Function.identity(), (a, b) -> a));

        return salaRepository.findAll().stream()
                .map(s -> {
                    ReservaSala r = enCurso.get(s.getIdSala());
                    String estado;
                    if (!"DISPONIBLE".equals(s.getEstado())) {
                        estado = "MANTENIMIENTO";
                    } else if (r != null) {
                        estado = "OCUPADA";
                    } else {
                        estado = "LIBRE";
                    }
                    return new SalaEstadoDTO(s.getIdSala(), s.getNombre(), s.getCapacidadMin(), s.getCapacidadMax(),
                            estado, r != null ? r.getHoraFin() : null);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioSalaDTO> horariosSala(Long idSala, int duracionMinutos) {
        if (!DURACIONES.contains(duracionMinutos)) {
            throw new IllegalArgumentException("La duracion debe ser 60, 90 o 120 minutos");
        }
        Sala sala = salaRepository.findById(idSala)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro la sala con id " + idSala));
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = horaActual();

        List<HorarioSalaDTO> horarios = new ArrayList<>();
        // Horarios consecutivos desde la apertura, del tamano de la duracion elegida, hasta el cierre
        for (LocalTime inicio = APERTURA; !inicio.plusMinutes(duracionMinutos).isAfter(CIERRE);
             inicio = inicio.plusMinutes(duracionMinutos)) {
            LocalTime fin = inicio.plusMinutes(duracionMinutos);
            String estado;
            if (!"DISPONIBLE".equals(sala.getEstado())) {
                estado = "MANTENIMIENTO";
            } else if (inicio.plusMinutes(MARGEN_MINUTOS).isBefore(ahora)) {
                estado = "PASADO"; // ya comenzo hace mas de 5 minutos
            } else if (!reservaSalaRepository.findConflictos(idSala, 0L, hoy, inicio, fin).isEmpty()) {
                estado = "OCUPADO";
            } else {
                estado = "LIBRE";
            }
            horarios.add(new HorarioSalaDTO(inicio, fin, estado));
        }
        return horarios;
    }

    // ---------------- reglas ----------------

    private LocalTime horaActual() {
        return LocalTime.now().withSecond(0).withNano(0);
    }

    private void validarTamanoGrupo(int personas) {
        if (personas < MIN_PERSONAS || personas > MAX_PERSONAS) {
            throw new IllegalArgumentException("El grupo debe tener entre " + MIN_PERSONAS + " y " + MAX_PERSONAS
                    + " estudiantes, incluido el responsable");
        }
    }

    private Ventana calcularVentana(int duracionMinutos) {
        if (!DURACIONES.contains(duracionMinutos)) {
            throw new IllegalArgumentException("La duracion debe ser 60, 90 o 120 minutos");
        }
        LocalDate hoy = LocalDate.now();
        LocalTime inicio = horaActual();
        LocalTime fin = inicio.plusMinutes(duracionMinutos);
        if (inicio.isBefore(APERTURA) || fin.isBefore(inicio) || fin.isAfter(CIERRE)) {
            throw new BusinessRuleException("Las reservas se hacen de " + APERTURA + " a " + CIERRE
                    + " y deben terminar antes del cierre");
        }
        return new Ventana(hoy, inicio, fin);
    }

    // Horario elegido por el estudiante: hoy, dentro del horario de atencion y que no haya comenzado hace mas de 5 minutos
    private Ventana calcularVentanaElegida(LocalTime inicio, int duracionMinutos) {
        if (!DURACIONES.contains(duracionMinutos)) {
            throw new IllegalArgumentException("La duracion debe ser 60, 90 o 120 minutos");
        }
        LocalTime fin = inicio.plusMinutes(duracionMinutos);
        if (inicio.isBefore(APERTURA) || fin.isBefore(inicio) || fin.isAfter(CIERRE)) {
            throw new BusinessRuleException("Las reservas se hacen de " + APERTURA + " a " + CIERRE
                    + " y deben terminar antes del cierre");
        }
        if (inicio.plusMinutes(MARGEN_MINUTOS).isBefore(horaActual())) {
            throw new BusinessRuleException("Ese horario ya comenzo. Elige uno mas tarde");
        }
        return new Ventana(LocalDate.now(), inicio, fin);
    }

    private Usuario obtenerEstudianteHabilitado(String codigoUni) {
        String codigo = codigoUni == null ? "" : codigoUni.trim();
        Usuario u = usuarioRepository.findByCodigoUni(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro un estudiante con el codigo " + codigo));
        if (u.getRol() != Rol.ESTUDIANTE) {
            throw new BusinessRuleException("El codigo " + codigo + " no pertenece a un estudiante");
        }
        if (u.getEstadoAcceso() == null || u.getEstadoAcceso() != 1) {
            throw new BusinessRuleException("El estudiante " + codigo + " tiene el acceso bloqueado");
        }
        if (u.getEstadoMatricula() == null || u.getEstadoMatricula() != 1) {
            throw new BusinessRuleException("El estudiante " + codigo + " no tiene matricula vigente");
        }
        return u;
    }

    private void verificarSinReservaActiva(String codigo, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        List<ReservaSala> activas = reservaSalaRepository.findActivasDeEstudiante(fecha, inicio, fin, codigo);
        if (!activas.isEmpty()) {
            throw new BusinessRuleException("El estudiante " + codigo + " ya tiene una reserva activa hasta las "
                    + activas.get(0).getHoraFin());
        }
    }

    // Primera sala sin cruces, dejando el margen indicado (minutos) antes y despues
    private Optional<Sala> buscarSalaLibre(List<Sala> candidatas, Ventana v, int margen) {
        for (Sala s : candidatas) {
            List<ReservaSala> conflictos = reservaSalaRepository.findConflictos(
                    s.getIdSala(), 0L, v.fecha(),
                    v.inicio().minusMinutes(margen), v.fin().plusMinutes(margen));
            if (conflictos.isEmpty()) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }

    private EstudianteValidadoDTO convertToDTO(Usuario u) {
        return new EstudianteValidadoDTO(
                u.getCodigoUni(),
                u.getPersona().getNombres() + " " + u.getPersona().getApellidos(),
                u.getEscuelaProf() != null ? u.getEscuelaProf().getNombreEsc() : null
        );
    }
}