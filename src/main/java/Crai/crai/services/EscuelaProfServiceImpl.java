package Crai.crai.services;

import Crai.crai.dto.EscuelaProfDTO;
import Crai.crai.entity.EscuelaProf;
import Crai.crai.repository.EscuelaProfRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EscuelaProfServiceImpl implements IEscuelaProfService {
    private final EscuelaProfRepository escuelaProfRepository;

    public EscuelaProfServiceImpl(EscuelaProfRepository escuelaProfRepository) {
        this.escuelaProfRepository = escuelaProfRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EscuelaProfDTO> listarTodo() {
        return escuelaProfRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EscuelaProfDTO> buscarPorId(Long id) {
        return escuelaProfRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional
    public EscuelaProfDTO crear(EscuelaProfDTO dto) {
        EscuelaProf e = new EscuelaProf();
        e.setNombreEsc(dto.nombreEsc());
        e.setEstado(dto.estado() != null ? dto.estado() : "A");
        return convertToDTO(escuelaProfRepository.save(e));
    }

    @Override
    @Transactional
    public Optional<EscuelaProfDTO> actualizar(Long id, EscuelaProfDTO dto) {
        return escuelaProfRepository.findById(id).map(e -> {
            e.setNombreEsc(dto.nombreEsc());
            if (dto.estado() != null) {
                e.setEstado(dto.estado());
            }
            return convertToDTO(escuelaProfRepository.save(e));
        });
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {
        if (escuelaProfRepository.existsById(id)) {
            escuelaProfRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private EscuelaProfDTO convertToDTO(EscuelaProf e) {
        return new EscuelaProfDTO(e.getIdEscuelaProf(), e.getNombreEsc(), e.getEstado());
    }
}
