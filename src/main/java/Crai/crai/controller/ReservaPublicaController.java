package Crai.crai.controller;

import Crai.crai.dto.*;
import Crai.crai.services.IReservaPublicaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Flujo de reserva que usa el estudiante: grupo -> sala y actividad -> confirmar
@RestController
@RequestMapping("/api/v1/reservas-publicas")
public class ReservaPublicaController {
    private final IReservaPublicaService reservaPublicaService;

    public ReservaPublicaController(IReservaPublicaService reservaPublicaService) {
        this.reservaPublicaService = reservaPublicaService;
    }

    // Paso 1: boton "Validar" de cada estudiante
    @GetMapping("/estudiantes/{codigoUni}")
    public ResponseEntity<EstudianteValidadoDTO> validarEstudiante(@PathVariable String codigoUni) {
        return ResponseEntity.ok(reservaPublicaService.validarEstudiante(codigoUni));
    }

    // Paso 2: al elegir la duracion, devuelve la sala que se asignaria
    // personas = total del grupo, incluido el responsable
    @GetMapping("/disponibilidad")
    public ResponseEntity<DisponibilidadDTO> disponibilidad(@RequestParam int duracionMinutos,
                                                            @RequestParam int personas) {
        return ResponseEntity.ok(reservaPublicaService.consultarDisponibilidad(duracionMinutos, personas));
    }

    // Confirmar y registrar la reserva
    @PostMapping
    public ResponseEntity<ReservaPublicaResultadoDTO> reservar(@Valid @RequestBody ReservaPublicaDTO dto) {
        return new ResponseEntity<>(reservaPublicaService.reservar(dto), HttpStatus.CREATED);
    }

    // Pantalla "Ver salas": libre u ocupada hasta que hora
    @GetMapping("/salas-estado")
    public ResponseEntity<List<SalaEstadoDTO>> estadoSalas() {
        return ResponseEntity.ok(reservaPublicaService.estadoSalas());
    }
    @GetMapping("/salas/{idSala}/horarios")
    public ResponseEntity<List<HorarioSalaDTO>> horarios(@PathVariable Long idSala,
                                                         @RequestParam int duracionMinutos) {
        return ResponseEntity.ok(reservaPublicaService.horariosSala(idSala, duracionMinutos));
    }
}
