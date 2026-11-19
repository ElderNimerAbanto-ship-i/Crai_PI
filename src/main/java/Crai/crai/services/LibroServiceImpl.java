package Crai.crai.services;

import Crai.crai.dto.LibroDTO;
import Crai.crai.entity.Categoria;
import Crai.crai.entity.Libro;
import Crai.crai.exception.BusinessRuleException;
import Crai.crai.exception.ResourceNotFoundException;
import Crai.crai.repository.CategoriaRepository;
import Crai.crai.repository.EjemplarRepository;
import Crai.crai.repository.LibroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LibroServiceImpl implements ILibroService {
    private final LibroRepository libroRepository;
    private final CategoriaRepository categoriaRepository;
    private final EjemplarRepository ejemplarRepository;
    private final IFileStorageService fileStorageService;

    public LibroServiceImpl(LibroRepository libroRepository, CategoriaRepository categoriaRepository,
                            EjemplarRepository ejemplarRepository, IFileStorageService fileStorageService) {
        this.libroRepository = libroRepository;
        this.categoriaRepository = categoriaRepository;
        this.ejemplarRepository = ejemplarRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibroDTO> listarTodo(String buscar) {
        List<Libro> libros = (buscar == null || buscar.isBlank())
                ? libroRepository.findAll()
                : libroRepository.findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCase(buscar, buscar);
        return libros.stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LibroDTO> buscarPorId(Long id) {
        return libroRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional
    public LibroDTO crear(LibroDTO dto) {
        Categoria cat = categoriaRepository.findById(dto.idCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id:" + dto.idCategoria()));
        Libro l = new Libro();
        l.setTitulo(dto.titulo());
        l.setAutor(dto.autor());
        l.setSeccionEspecial(dto.seccionEspecial());
        l.setImagen(dto.imagen()); // nombre del archivo que viene del controlador
        l.setCategoria(cat);
        return convertToDTO(libroRepository.save(l));
    }

    @Override
    @Transactional
    public Optional<LibroDTO> actualizar(Long id, LibroDTO dto) {
        return libroRepository.findById(id).map(l -> {
            Categoria cat = categoriaRepository.findById(dto.idCategoria())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id:" + dto.idCategoria()));
            // Si la imagen cambió, borramos el archivo físico anterior para no dejar basura
            if (l.getImagen() != null && !l.getImagen().equals(dto.imagen())) {
                fileStorageService.delete(l.getImagen());
            }
            l.setTitulo(dto.titulo());
            l.setAutor(dto.autor());
            l.setSeccionEspecial(dto.seccionEspecial());
            l.setImagen(dto.imagen());
            l.setCategoria(cat);
            return convertToDTO(libroRepository.save(l));
        });
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {
        return libroRepository.findById(id).map(l -> {
            if (ejemplarRepository.existsByLibroIdLibro(id)) {
                throw new BusinessRuleException("No se puede eliminar un libro que tiene ejemplares registrados");
            }
            // Borrar archivo físico
            if (l.getImagen() != null) {
                fileStorageService.delete(l.getImagen());
            }
            libroRepository.delete(l);
            return true;
        }).orElse(false);
    }

    private LibroDTO convertToDTO(Libro l) {
        // Devolvemos solo la ruta relativa de la imagen (null si el libro no tiene portada)
        String relativePath = l.getImagen() != null ? "/crai/images/" + l.getImagen() : null;

        return new LibroDTO(
                l.getIdLibro(),
                l.getTitulo(),
                l.getAutor(),
                l.getSeccionEspecial(),
                l.getCategoria().getIdCategoria(),
                l.getCategoria().getNombre(),
                relativePath,
                ejemplarRepository.countByLibroIdLibroAndEstado(l.getIdLibro(), "DISPONIBLE")
        );
    }
}
