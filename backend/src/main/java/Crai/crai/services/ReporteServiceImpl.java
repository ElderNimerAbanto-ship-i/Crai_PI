package Crai.crai.services;

import Crai.crai.dto.*;
import Crai.crai.entity.Rol;
import Crai.crai.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ReporteServiceImpl implements IReporteService {
    private static final String SIN_ESCUELA = "Sin escuela";
    private static final int HORA_INICIAL = 8;  // apertura del CRAI
    private static final int HORA_FINAL = 21;   // cierre del CRAI

    private final LibroRepository libroRepository;
    private final EjemplarRepository ejemplarRepository;
    private final UsuarioRepository usuarioRepository;
    private final PrestamoRepository prestamoRepository;
    private final DetallePrestamoRepository detallePrestamoRepository;
    private final ReservaSalaRepository reservaSalaRepository;
    private final ControlIngresoRepository controlIngresoRepository;
    private final ReservaIntegranteRepository reservaIntegranteRepository;
    private final SalaRepository salaRepository;
    private final LibroMasPrestadoRepository libroMasPrestadoRepository; // <-- NUEVO REPO DE LA VISTA

    public ReporteServiceImpl(LibroRepository libroRepository, EjemplarRepository ejemplarRepository,
                              UsuarioRepository usuarioRepository, PrestamoRepository prestamoRepository,
                              DetallePrestamoRepository detallePrestamoRepository,
                              ReservaSalaRepository reservaSalaRepository,
                              ControlIngresoRepository controlIngresoRepository,
                              ReservaIntegranteRepository reservaIntegranteRepository,
                              SalaRepository salaRepository,
                              LibroMasPrestadoRepository libroMasPrestadoRepository) {
        this.libroRepository = libroRepository;
        this.ejemplarRepository = ejemplarRepository;
        this.usuarioRepository = usuarioRepository;
        this.prestamoRepository = prestamoRepository;
        this.detallePrestamoRepository = detallePrestamoRepository;
        this.reservaSalaRepository = reservaSalaRepository;
        this.controlIngresoRepository = controlIngresoRepository;
        this.reservaIntegranteRepository = reservaIntegranteRepository;
        this.salaRepository = salaRepository;
        this.libroMasPrestadoRepository = libroMasPrestadoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenDTO resumen() {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicioDia = hoy.atStartOfDay();
        return new ResumenDTO(
                libroRepository.count(),
                ejemplarRepository.count(),
                ejemplarRepository.countByEstado("DISPONIBLE"),
                usuarioRepository.count(),
                prestamoRepository.countByEstado("A"),
                prestamoRepository.countByEstadoAndFechaVencimientoBefore("A", LocalDateTime.now()),
                reservaSalaRepository.countByFechaReservaAndEstado(hoy, "A"),
                // solo entradas: cada visita genera una ENTRADA y una SALIDA
                controlIngresoRepository.contarEntradas(inicioDia, inicioDia.plusDays(1), Rol.ADMIN)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroPrestadoDTO> librosMasPrestados(int limite) {
        if (limite < 1 || limite > 50) {
            throw new IllegalArgumentException("El limite debe estar entre 1 y 50");
        }
        // Consultamos directo a la vista a través del nuevo repositorio mapeado al DTO
        return libroMasPrestadoRepository.obtenerLibrosMasPrestados(PageRequest.of(0, limite));
    }

    // ---------------- Dashboard ----------------

    @Override
    @Transactional(readOnly = true)
    public List<UsoEscuelaDTO> usoPorEscuela(LocalDate desde, LocalDate hasta) {
        LocalDate[] r = rango(desde, hasta);
        LocalDateTime inicio = r[0].atStartOfDay();
        LocalDateTime fin = r[1].plusDays(1).atStartOfDay();

        Map<String, long[]> acumulado = new HashMap<>(); // [ingresos, prestamos, reservas]
        sumar(acumulado, controlIngresoRepository.ingresosPorEscuela(inicio, fin, Rol.ADMIN), 0);
        sumar(acumulado, prestamoRepository.prestamosPorEscuela(inicio, fin, Rol.ADMIN), 1);
        sumar(acumulado, reservaSalaRepository.reservasPorEscuelaResponsable(r[0], r[1]), 2);
        sumar(acumulado, reservaIntegranteRepository.integrantesPorEscuela(r[0], r[1]), 2);

        return acumulado.entrySet().stream()
                .map(e -> {
                    long[] v = e.getValue();
                    return new UsoEscuelaDTO(e.getKey(), v[0], v[1], v[2], v[0] + v[1] + v[2]);
                })
                // el ranking principal son los ingresos
                .sorted(Comparator.comparingLong(UsoEscuelaDTO::ingresos).reversed()
                        .thenComparing(Comparator.comparingLong(UsoEscuelaDTO::total).reversed()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsoSalaDTO> usoSalas(LocalDate desde, LocalDate hasta) {
        LocalDate[] r = rango(desde, hasta);
        Map<Long, long[]> acumulado = new HashMap<>(); // [reservas, minutos]
        for (Object[] f : reservaSalaRepository.reservasActivasEntre(r[0], r[1])) {
            long minutos = Duration.between((LocalTime) f[3], (LocalTime) f[4]).toMinutes();
            long[] a = acumulado.computeIfAbsent(((Number) f[0]).longValue(), k -> new long[2]);
            a[0]++;
            a[1] += minutos;
        }
        // se incluyen todas las salas, incluso las que no tuvieron reservas
        return salaRepository.findAll().stream()
                .map(s -> {
                    long[] a = acumulado.getOrDefault(s.getIdSala(), new long[2]);
                    return new UsoSalaDTO(s.getIdSala(), s.getNombre(), a[0], Math.round(a[1] / 6.0) / 10.0);
                })
                .sorted(Comparator.comparingLong(UsoSalaDTO::reservas).reversed().thenComparing(UsoSalaDTO::sala))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioUsoDTO> usoHorarios(LocalDate desde, LocalDate hasta) {
        LocalDate[] r = rango(desde, hasta);
        Map<Integer, Long> celdas = new TreeMap<>(); // clave = dia * 100 + hora
        for (Object[] f : reservaSalaRepository.reservasActivasEntre(r[0], r[1])) {
            int dia = ((LocalDate) f[2]).getDayOfWeek().getValue();
            LocalTime inicio = (LocalTime) f[3];
            LocalTime fin = (LocalTime) f[4];
            // la reserva cuenta en cada hora que cubre
            for (int h = HORA_INICIAL; h < HORA_FINAL; h++) {
                if (inicio.isBefore(LocalTime.of(h + 1, 0)) && fin.isAfter(LocalTime.of(h, 0))) {
                    celdas.merge(dia * 100 + h, 1L, Long::sum);
                }
            }
        }
        return celdas.entrySet().stream()
                .map(e -> new HorarioUsoDTO(e.getKey() / 100, e.getKey() % 100, e.getValue()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoMesDTO> prestamosPorMes(int meses) {
        if (meses < 1 || meses > 24) {
            throw new IllegalArgumentException("Los meses deben estar entre 1 y 24");
        }
        YearMonth primero = YearMonth.now().minusMonths(meses - 1L);
        Map<YearMonth, Long> conteo = new HashMap<>();
        // una fila por ejemplar prestado: se cuentan libros, no prestamos
        for (LocalDateTime f : detallePrestamoRepository.fechasDePrestamo(primero.atDay(1).atStartOfDay())) {
            conteo.merge(YearMonth.from(f), 1L, Long::sum);
        }
        List<PrestamoMesDTO> resultado = new ArrayList<>();
        for (int i = 0; i < meses; i++) {
            YearMonth m = primero.plusMonths(i);
            resultado.add(new PrestamoMesDTO(m.toString(), conteo.getOrDefault(m, 0L)));
        }
        return resultado;
    }

    // Rango pedido (por defecto, los ultimos 30 dias) validado
    private LocalDate[] rango(LocalDate desde, LocalDate hasta) {
        LocalDate fin = hasta != null ? hasta : LocalDate.now();
        LocalDate inicio = desde != null ? desde : fin.minusDays(29);
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la final");
        }
        if (ChronoUnit.DAYS.between(inicio, fin) > 730) {
            throw new IllegalArgumentException("El rango no puede superar 2 anios");
        }
        return new LocalDate[]{inicio, fin};
    }

    // Suma filas (escuela, cantidad) en la posicion indicada; escuela null = "Sin escuela"
    private void sumar(Map<String, long[]> acumulado, List<Object[]> filas, int posicion) {
        for (Object[] f : filas) {
            String nombre = f[0] == null ? SIN_ESCUELA : (String) f[0];
            acumulado.computeIfAbsent(nombre, k -> new long[3])[posicion] += ((Number) f[1]).longValue();
        }
    }
}
