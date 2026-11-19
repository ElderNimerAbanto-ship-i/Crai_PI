package Crai.crai.controller;

import Crai.crai.dto.EjemplarDTO;
import Crai.crai.exception.ResourceNotFoundException;
import Crai.crai.services.IEjemplarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ejemplares")
public class EjemplarController {
    private final IEjemplarService ejemplarService;

    public EjemplarController(IEjemplarService ejemplarService) {
        this.ejemplarService = ejemplarService;
    }

    @GetMapping
    public ResponseEntity<List<EjemplarDTO>> listar() {
        return ResponseEntity.ok(ejemplarService.listarTodo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EjemplarDTO> obtener(@PathVariable Long id) {
        return ejemplarService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Ejemplar no encontrado con id:" + id));
    }

    @PostMapping
    public ResponseEntity<EjemplarDTO> crear(@Valid @RequestBody EjemplarDTO dto) {
        return new ResponseEntity<>(ejemplarService.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EjemplarDTO> actualizar(@PathVariable Long id, @Valid @RequestBody EjemplarDTO dto) {
        return ejemplarService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("No se pudo actualizar con id:" + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (ejemplarService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        throw new ResourceNotFoundException("No se pudo eliminar con id:" + id);
    }
}
