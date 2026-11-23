package Crai.crai.controller;

import Crai.crai.dto.ControlIngresoDTO;
import Crai.crai.exception.ResourceNotFoundException;
import Crai.crai.services.IControlIngresoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ingresos")
public class ControlIngresoController {
    private final IControlIngresoService controlIngresoService;

    public ControlIngresoController(IControlIngresoService controlIngresoService) {
        this.controlIngresoService = controlIngresoService;
    }

    // Sin try/catch: los errores (usuario no encontrado, acceso denegado, ORA-20020 del trigger)
    // los convierte GlobalExceptionHandler en JSON {status, error, mensaje} con el codigo HTTP correcto.
    @PostMapping("/registrar")
    public ResponseEntity<ControlIngresoDTO> registrar(@Valid @RequestBody ControlIngresoDTO dto) {
        return ResponseEntity.ok(controlIngresoService.registrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<ControlIngresoDTO>> listarTodo() {
        return ResponseEntity.ok(controlIngresoService.listarTodo());
    }

    @GetMapping("/persona/{idPersona}")
    public ResponseEntity<List<ControlIngresoDTO>> listarPorPersona(@PathVariable Long idPersona) {
        return ResponseEntity.ok(controlIngresoService.listarPorPersona(idPersona));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ControlIngresoDTO> buscarPorId(@PathVariable Long id) {
        return controlIngresoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
