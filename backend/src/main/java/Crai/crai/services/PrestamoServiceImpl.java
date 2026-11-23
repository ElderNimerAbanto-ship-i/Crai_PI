package Crai.crai.services;

import Crai.crai.dto.DetallePrestamoDTO;
import Crai.crai.dto.PrestamoDTO;
import Crai.crai.dto.PrestamoProcedimientoDTO;
import Crai.crai.dto.PrestamoRegistroDTO;
import Crai.crai.entity.DetallePrestamo;
import Crai.crai.entity.Ejemplar;
import Crai.crai.entity.Prestamo;
import Crai.crai.entity.Usuario;
import Crai.crai.exception.BusinessRuleException;
import Crai.crai.exception.ResourceNotFoundException;
import Crai.crai.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class PrestamoServiceImpl implements IPrestamoService {
    private static final String ACTIVO = "A";
    private static final String DEVUELTO = "D";
    private static final int MAX_PRESTAMOS_ACTIVOS = 3;
    private static final int MAX_DIAS_RENOVACION = 15;
    // El procedimiento vive en el esquema CRAI, igual que las tablas (@Table(schema = "CRAI")),
    // porque el backend se conecta con otro usuario (US_CRAI).
    private static final String SP_REGISTRAR_PRESTAMO = "CRAI.sp_registrar_prestamo";

    private final EntityManager entityManager;
    private final PrestamoRepository prestamoRepository;
    private final DetallePrestamoRepository detallePrestamoRepository;
    private final EjemplarRepository ejemplarRepository;
    private final UsuarioRepository usuarioRepository;
    private final PrestamoProcedimientoRepository prestamoProcedimientoRepository;

    public PrestamoServiceImpl(EntityManager entityManager,
                               PrestamoRepository prestamoRepository,
                               DetallePrestamoRepository detallePrestamoRepository,
                               EjemplarRepository ejemplarRepository,
                               UsuarioRepository usuarioRepository,
                               PrestamoProcedimientoRepository prestamoProcedimientoRepository) {
        this.entityManager = entityManager;
        this.prestamoRepository = prestamoRepository;
        this.detallePrestamoRepository = detallePrestamoRepository;
        this.ejemplarRepository = ejemplarRepository;
        this.usuarioRepository = usuarioRepository;
        this.prestamoProcedimientoRepository = prestamoProcedimientoRepository;
    }

    @Override
    @Transactional
    public PrestamoDTO registrarConProcedimiento(PrestamoProcedimientoDTO dto) {
        Long idPrestamo = prestamoProcedimientoRepository.registrarPrestamo(
                dto.codigoUni(), dto.idEjemplar(), dto.dias());

        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new IllegalStateException("El prestamo " + idPrestamo + " no se encontro tras registrarlo"));
        return convertToDTO(prestamo);
    }

//    @Override
//    @Transactional
//    public PrestamoDTO registrar(PrestamoRegistroDTO dto) {
//        Usuario u = usuarioRepository.findByCodigoUni(dto.codigoUni().trim())
//                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con codigo: " + dto.codigoUni()));
//        Ejemplar ej = ejemplarRepository.findById(dto.idEjemplar())
//                .orElseThrow(() -> new ResourceNotFoundException("Ejemplar no encontrado con id: " + dto.idEjemplar()));
//
//        // Reglas de negocio (el procedimiento solo inserta; estas reglas viven en el service)
//        if (u.getEstadoAcceso() == null || u.getEstadoAcceso() != 1) {
//            throw new BusinessRuleException("El usuario no tiene acceso habilitado para pedir prestamos");
//        }
//        Long idPersona = u.getIdPersona();
//        if (prestamoRepository.existsByPersonaIdPersonaAndEstadoAndFechaVencimientoBefore(idPersona, ACTIVO, LocalDateTime.now())) {
//            throw new BusinessRuleException("El usuario tiene prestamos vencidos pendientes de devolucion");
//        }
//        if (prestamoRepository.countByPersonaIdPersonaAndEstado(idPersona, ACTIVO) >= MAX_PRESTAMOS_ACTIVOS) {
//            throw new BusinessRuleException("El usuario alcanzo el maximo de " + MAX_PRESTAMOS_ACTIVOS + " prestamos activos");
//        }
//        if (!"DISPONIBLE".equalsIgnoreCase(ej.getEstado())) {
//            throw new BusinessRuleException("El ejemplar no esta disponible para prestamo");
//        }
//
//        // sp_registrar_prestamo(PV_ID_PERSONA, PV_ID_EJEMPLAR, PV_DIAS, PV_ID_PRESTAMO OUT)
//        // Recibe el ID de la persona (no el codigo universitario).
//        StoredProcedureQuery query = entityManager.createStoredProcedureQuery(SP_REGISTRAR_PRESTAMO);
//        query.registerStoredProcedureParameter(1, Long.class, ParameterMode.IN);
//        query.registerStoredProcedureParameter(2, Long.class, ParameterMode.IN);
//        query.registerStoredProcedureParameter(3, Integer.class, ParameterMode.IN);
//        query.registerStoredProcedureParameter(4, Long.class, ParameterMode.OUT);
//        query.setParameter(1, idPersona);
//        query.setParameter(2, ej.getIdEjemplar());
//        query.setParameter(3, dto.diasPrestamo());
//        query.execute();
//
//        Long idPrestamo = ((Number) query.getOutputParameterValue(4)).longValue();
//
//        // El ejemplar queda PRESTADO. Se hace aqui para que funcione con o sin trigger en detalle_prestamo.
//        ej.setEstado("PRESTADO");
//        ejemplarRepository.save(ej);
//
//        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
//                .orElseThrow(() -> new IllegalStateException("El prestamo " + idPrestamo + " no se encontro tras registrarlo"));
//        return convertToDTO(prestamo);
//    }

    @Override
    @Transactional
    public PrestamoDTO devolver(Long idEjemplar) {
        Ejemplar ej = ejemplarRepository.findById(idEjemplar)
                .orElseThrow(() -> new ResourceNotFoundException("Ejemplar no encontrado con id: " + idEjemplar));
        DetallePrestamo d = detallePrestamoRepository.findByEjemplarIdEjemplarAndPrestamoEstado(idEjemplar, ACTIVO)
                .orElseThrow(() -> new BusinessRuleException("No existe un prestamo activo para este ejemplar"));

        Prestamo p = d.getPrestamo();
        p.setEstado(DEVUELTO);
        p.setFechaDevolucion(LocalDateTime.now());
        prestamoRepository.save(p);

        ej.setEstado("DISPONIBLE");
        ejemplarRepository.save(ej);
        return convertToDTO(p);
    }

    @Override
    @Transactional
    public PrestamoDTO renovar(Long idPrestamo, int diasExtension) {
        if (diasExtension < 1 || diasExtension > MAX_DIAS_RENOVACION) {
            throw new IllegalArgumentException("Los dias de extension deben estar entre 1 y " + MAX_DIAS_RENOVACION);
        }
        Prestamo p = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new ResourceNotFoundException("Prestamo no encontrado con id: " + idPrestamo));
        if (!ACTIVO.equals(p.getEstado())) {
            throw new BusinessRuleException("Solo se puede renovar un prestamo activo");
        }
        if (p.getFechaVencimiento().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("No se puede renovar un prestamo vencido, debe devolverse primero");
        }
        p.setFechaVencimiento(p.getFechaVencimiento().plusDays(diasExtension));
        return convertToDTO(prestamoRepository.save(p));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoDTO> listarTodo() {
        return prestamoRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoDTO> listarActivos() {
        return prestamoRepository.findByEstado(ACTIVO).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoDTO> listarVencidos() {
        return prestamoRepository.findByEstadoAndFechaVencimientoBefore(ACTIVO, LocalDateTime.now()).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoDTO> listarPorPersona(Long idPersona) {
        return prestamoRepository.findByPersonaIdPersona(idPersona).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PrestamoDTO> buscarPorId(Long id) {
        return prestamoRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetallePrestamoDTO> listarDetalle(Long idPrestamo) {
        if (!prestamoRepository.existsById(idPrestamo)) {
            throw new ResourceNotFoundException("Prestamo no encontrado con id: " + idPrestamo);
        }
        return detallePrestamoRepository.findByPrestamoIdPrestamo(idPrestamo).stream()
                .map(d -> new DetallePrestamoDTO(
                        d.getIdDetalle(),
                        d.getPrestamo().getIdPrestamo(),
                        d.getEjemplar().getIdEjemplar(),
                        d.getEjemplar().getIsbn(),
                        d.getEjemplar().getLibro().getTitulo()))
                .toList();
    }

    private PrestamoDTO convertToDTO(Prestamo p) {
        return new PrestamoDTO(
                p.getIdPrestamo(),
                p.getFechaPrestamo(),
                p.getFechaVencimiento(),
                p.getFechaDevolucion(),
                p.getEstado(),
                p.getPersona().getIdPersona(),
                p.getPersona().getNombres() + " " + p.getPersona().getApellidos()
        );
    }
}
