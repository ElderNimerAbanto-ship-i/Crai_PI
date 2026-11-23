package Crai.crai.controller;

import Crai.crai.dto.EscuelaProfDTO;
import Crai.crai.exception.ResourceNotFoundException;
import Crai.crai.services.IEscuelaProfService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/escuelas")
public class EscuelaProfController {
    private final IEscuelaProfService escuelaProfService;

    public EscuelaProfController(IEscuelaProfService escuelaProfService) {
        this.escuelaProfService = escuelaProfService;
    }

    @GetMapping
    public ResponseEntity<List<EscuelaProfDTO>> listar() {
        return ResponseEntity.ok(escuelaProfService.listarTodo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EscuelaProfDTO> obtener(@PathVariable Long id) {
        return escuelaProfService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Escuela no encontrada con id:" + id));
    }

    @PostMapping
    public ResponseEntity<EscuelaProfDTO> crear(@Valid @RequestBody EscuelaProfDTO dto) {
        return new ResponseEntity<>(escuelaProfService.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EscuelaProfDTO> actualizar(@PathVariable Long id, @Valid @RequestBody EscuelaProfDTO dto) {
        return escuelaProfService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("No se pudo actualizar con id:" + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (escuelaProfService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        throw new ResourceNotFoundException("No se pudo eliminar con id:" + id);
    }
}
