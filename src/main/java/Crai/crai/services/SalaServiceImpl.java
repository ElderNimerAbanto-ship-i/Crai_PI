package Crai.crai.services;

import Crai.crai.dto.SalaDTO;
import Crai.crai.entity.Sala;
import Crai.crai.exception.DuplicateResourceException;
import Crai.crai.repository.SalaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SalaServiceImpl implements ISalaService {
    private final SalaRepository salaRepository;

    public SalaServiceImpl(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalaDTO> listarTodo() {
        return salaRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SalaDTO> buscarPorId(Long id) {
        return salaRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional
    public SalaDTO crear(SalaDTO dto) {
        validarCapacidad(dto);
        if (salaRepository.existsByNombreIgnoreCase(dto.nombre())) {
            throw new DuplicateResourceException("La sala '" + dto.nombre() + "' ya existe");
        }
        Sala s = new Sala();
        s.setNombre(dto.nombre());
        s.setCapacidadMin(dto.capacidadMin());
        s.setCapacidadMax(dto.capacidadMax());
        s.setEstado(dto.estado() != null ? dto.estado() : "DISPONIBLE");
        return convertToDTO(salaRepository.save(s));
    }

    @Override
    @Transactional
    public Optional<SalaDTO> actualizar(Long id, SalaDTO dto) {
        validarCapacidad(dto);
        return salaRepository.findById(id).map(s -> {
            if (!s.getNombre().equalsIgnoreCase(dto.nombre())
                    && salaRepository.existsByNombreIgnoreCase(dto.nombre())) {
                throw new DuplicateResourceException("La sala '" + dto.nombre() + "' ya existe");
            }
            s.setNombre(dto.nombre());
            s.setCapacidadMin(dto.capacidadMin());
            s.setCapacidadMax(dto.capacidadMax());
            if (dto.estado() != null) {
                s.setEstado(dto.estado());
            }
            return convertToDTO(salaRepository.save(s));
        });
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {
        if (salaRepository.existsById(id)) {
            salaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void validarCapacidad(SalaDTO dto) {
        if (dto.capacidadMin() > dto.capacidadMax()) {
            throw new IllegalArgumentException("La capacidad minima no puede ser mayor que la maxima");
        }
    }

    private SalaDTO convertToDTO(Sala s) {
        return new SalaDTO(s.getIdSala(), s.getNombre(), s.getCapacidadMin(), s.getCapacidadMax(), s.getEstado());
    }
}
