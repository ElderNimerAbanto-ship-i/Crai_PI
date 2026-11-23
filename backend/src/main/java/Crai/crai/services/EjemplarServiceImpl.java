package Crai.crai.services;

import Crai.crai.dto.EjemplarDTO;
import Crai.crai.entity.Ejemplar;
import Crai.crai.entity.Libro;
import Crai.crai.exception.BusinessRuleException;
import Crai.crai.exception.ResourceNotFoundException;
import Crai.crai.repository.EjemplarRepository;
import Crai.crai.repository.LibroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EjemplarServiceImpl implements IEjemplarService {
    private final EjemplarRepository ejemplarRepository;
    private final LibroRepository libroRepository;

    public EjemplarServiceImpl(EjemplarRepository ejemplarRepository, LibroRepository libroRepository) {
        this.ejemplarRepository = ejemplarRepository;
        this.libroRepository = libroRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EjemplarDTO> listarTodo() {
        return ejemplarRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EjemplarDTO> buscarPorId(Long id) {
        return ejemplarRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional
    public EjemplarDTO crear(EjemplarDTO dto) {
        Libro libro = libroRepository.findById(dto.idLibro())
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id:" + dto.idLibro()));
        Ejemplar e = new Ejemplar();
        e.setEstado(dto.estado() != null ? dto.estado() : "DISPONIBLE");
        e.setIsbn(dto.isbn());
        e.setLibro(libro);
        return convertToDTO(ejemplarRepository.save(e));
    }

    @Override
    @Transactional
    public Optional<EjemplarDTO> actualizar(Long id, EjemplarDTO dto) {
        return ejemplarRepository.findById(id).map(e -> {
            Libro libro = libroRepository.findById(dto.idLibro())
                    .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id:" + dto.idLibro()));
            if (dto.estado() != null) {
                e.setEstado(dto.estado());
            }
            e.setIsbn(dto.isbn());
            e.setLibro(libro);
            return convertToDTO(ejemplarRepository.save(e));
        });
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {
        return ejemplarRepository.findById(id).map(e -> {
            if ("PRESTADO".equals(e.getEstado())) {
                throw new BusinessRuleException("No se puede eliminar un ejemplar que esta prestado");
            }
            ejemplarRepository.delete(e);
            return true;
        }).orElse(false);
    }

    private EjemplarDTO convertToDTO(Ejemplar e) {
        return new EjemplarDTO(
                e.getIdEjemplar(),
                e.getEstado(),
                e.getIsbn(),
                e.getLibro().getIdLibro(),
                e.getLibro().getTitulo()
        );
    }
}
