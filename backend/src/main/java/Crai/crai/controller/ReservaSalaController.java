package Crai.crai.controller;

import Crai.crai.dto.ReservaSalaDTO;
import Crai.crai.exception.ResourceNotFoundException;
import Crai.crai.services.IReservaSalaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservas-sala")
public class ReservaSalaController {
    private final IReservaSalaService reservaSalaService;

    public ReservaSalaController(IReservaSalaService reservaSalaService) {
        this.reservaSalaService = reservaSalaService;
    }

    @GetMapping
    public ResponseEntity<List<ReservaSalaDTO>> listar() {
        return ResponseEntity.ok(reservaSalaService.listarTodo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaSalaDTO> obtener(@PathVariable Long id) {
        return reservaSalaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva de sala no encontrada con id:" + id));
    }

    @PostMapping
    public ResponseEntity<ReservaSalaDTO> crear(@Valid @RequestBody ReservaSalaDTO dto) {
        return new ResponseEntity<>(reservaSalaService.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaSalaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ReservaSalaDTO dto) {
        return reservaSalaService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("No se pudo actualizar con id:" + id));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ReservaSalaDTO> cancelar(@PathVariable Long id) {
        return reservaSalaService.cancelar(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva de sala no encontrada con id:" + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (reservaSalaService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        throw new ResourceNotFoundException("No se pudo eliminar con id:" + id);
    }
}
