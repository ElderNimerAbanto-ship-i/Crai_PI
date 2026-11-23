package Crai.crai.controller;

import Crai.crai.dto.ReservaIntegranteDTO;
import Crai.crai.exception.ResourceNotFoundException;
import Crai.crai.services.IReservaIntegranteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservas-integrantes")
public class ReservaIntegranteController {
    private final IReservaIntegranteService reservaIntegranteService;

    public ReservaIntegranteController(IReservaIntegranteService reservaIntegranteService) {
        this.reservaIntegranteService = reservaIntegranteService;
    }

    @GetMapping
    public ResponseEntity<List<ReservaIntegranteDTO>> listar() {
        return ResponseEntity.ok(reservaIntegranteService.listarTodo());
    }

    @GetMapping("/reserva/{idReservaSala}")
    public ResponseEntity<List<ReservaIntegranteDTO>> listarPorReserva(@PathVariable Long idReservaSala) {
        return ResponseEntity.ok(reservaIntegranteService.listarPorReserva(idReservaSala));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaIntegranteDTO> obtener(@PathVariable Long id) {
        return reservaIntegranteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Integrante no encontrado con id:" + id));
    }

    @PostMapping
    public ResponseEntity<ReservaIntegranteDTO> crear(@Valid @RequestBody ReservaIntegranteDTO dto) {
        return new ResponseEntity<>(reservaIntegranteService.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaIntegranteDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ReservaIntegranteDTO dto) {
        return reservaIntegranteService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("No se pudo actualizar con id:" + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (reservaIntegranteService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        throw new ResourceNotFoundException("No se pudo eliminar con id:" + id);
    }
}
