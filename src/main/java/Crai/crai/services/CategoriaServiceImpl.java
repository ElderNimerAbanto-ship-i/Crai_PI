package Crai.crai.services;

import Crai.crai.dto.CategoriaDTO;
import Crai.crai.entity.Categoria;
import Crai.crai.exception.BusinessRuleException;
import Crai.crai.exception.DuplicateResourceException;
import Crai.crai.repository.CategoriaRepository;
import Crai.crai.repository.LibroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServiceImpl implements ICategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final LibroRepository libroRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, LibroRepository libroRepository) {
        this.categoriaRepository = categoriaRepository;
        this.libroRepository = libroRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarTodo() {
        return categoriaRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CategoriaDTO> buscarPorId(Long id) {
        return categoriaRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional
    public CategoriaDTO crear(CategoriaDTO dto) {
        if (categoriaRepository.existsByNombreIgnoreCase(dto.nombre())) {
            throw new DuplicateResourceException("La categoria '" + dto.nombre() + "' ya existe");
        }
        Categoria c = new Categoria();
        c.setNombre(dto.nombre());
        return convertToDTO(categoriaRepository.save(c));
    }

    @Override
    @Transactional
    public Optional<CategoriaDTO> actualizar(Long id, CategoriaDTO dto) {
        return categoriaRepository.findById(id).map(c -> {
            if (!c.getNombre().equalsIgnoreCase(dto.nombre())
                    && categoriaRepository.existsByNombreIgnoreCase(dto.nombre())) {
                throw new DuplicateResourceException("La categoria '" + dto.nombre() + "' ya existe");
            }
            c.setNombre(dto.nombre());
            return convertToDTO(categoriaRepository.save(c));
        });
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            return false;
        }
        if (libroRepository.existsByCategoriaIdCategoria(id)) {
            throw new BusinessRuleException("No se puede eliminar una categoria que tiene libros asociados");
        }
        categoriaRepository.deleteById(id);
        return true;
    }

    private CategoriaDTO convertToDTO(Categoria c) {
        return new CategoriaDTO(c.getIdCategoria(), c.getNombre());
    }
}
