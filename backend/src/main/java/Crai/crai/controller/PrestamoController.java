package Crai.crai.controller;

import Crai.crai.dto.DetallePrestamoDTO;
import Crai.crai.dto.PrestamoDTO;
import Crai.crai.dto.PrestamoProcedimientoDTO;
import Crai.crai.dto.PrestamoRegistroDTO;
import Crai.crai.services.IPrestamoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/prestamos")
public class PrestamoController {

    private final IPrestamoService prestamoService;

    public PrestamoController(IPrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

//    @PostMapping("/registrar")
//    public ResponseEntity<PrestamoDTO> registrarPrestamo(@Valid @RequestBody PrestamoRegistroDTO dto) {
//        PrestamoDTO nuevoPrestamo = prestamoService.registrar(dto);
//        return ResponseEntity.ok(nuevoPrestamo);
//    }
    // Registra el prestamo llamando directamente al procedimiento CRAI.sp_registrar_prestamo
    @PostMapping("/registrar-sp")
    public ResponseEntity<PrestamoDTO> registrarConProcedimiento(@Valid @RequestBody PrestamoProcedimientoDTO dto) {
        PrestamoDTO nuevoPrestamo = prestamoService.registrarConProcedimiento(dto);
        return ResponseEntity.ok(nuevoPrestamo);
    }
    @GetMapping
    public ResponseEntity<List<PrestamoDTO>> listarTodo() {
        return ResponseEntity.ok(prestamoService.listarTodo());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<PrestamoDTO>> listarActivos() {
        return ResponseEntity.ok(prestamoService.listarActivos());
    }

    @GetMapping("/vencidos")
    public ResponseEntity<List<PrestamoDTO>> listarVencidos() {
        return ResponseEntity.ok(prestamoService.listarVencidos());
    }

    @GetMapping("/persona/{idPersona}")
    public ResponseEntity<List<PrestamoDTO>> listarPorPersona(@PathVariable Long idPersona) {
        return ResponseEntity.ok(prestamoService.listarPorPersona(idPersona));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestamoDTO> buscarPorId(@PathVariable Long id) {
        return prestamoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{idPrestamo}/detalles")
    public ResponseEntity<List<DetallePrestamoDTO>> listarDetalle(@PathVariable Long idPrestamo) {
        return ResponseEntity.ok(prestamoService.listarDetalle(idPrestamo));
    }

    @PutMapping("/devolver/{idEjemplar}")
    public ResponseEntity<PrestamoDTO> devolver(@PathVariable Long idEjemplar) {
        return ResponseEntity.ok(prestamoService.devolver(idEjemplar));
    }

    @PutMapping("/renovar/{idPrestamo}")
    public ResponseEntity<PrestamoDTO> renovar(@PathVariable Long idPrestamo, @RequestParam int diasExtension) {
        return ResponseEntity.ok(prestamoService.renovar(idPrestamo, diasExtension));
    }

}