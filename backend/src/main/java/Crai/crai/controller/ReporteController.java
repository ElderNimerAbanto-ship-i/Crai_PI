package Crai.crai.controller;

import Crai.crai.dto.*;
import Crai.crai.services.IReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {
    private final IReporteService reporteService;

    public ReporteController(IReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenDTO> resumen() {
        return ResponseEntity.ok(reporteService.resumen());
    }

    @GetMapping("/libros-mas-prestados")
    public ResponseEntity<List<LibroPrestadoDTO>> librosMasPrestados(@RequestParam(defaultValue = "5") int limite) {
        return ResponseEntity.ok(reporteService.librosMasPrestados(limite));
    }

    // ---- Dashboard. desde y hasta son opcionales (yyyy-MM-dd); por defecto, los ultimos 30 dias ----

    @GetMapping("/uso-por-escuela")
    public ResponseEntity<List<UsoEscuelaDTO>> usoPorEscuela(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(reporteService.usoPorEscuela(desde, hasta));
    }

    @GetMapping("/uso-salas")
    public ResponseEntity<List<UsoSalaDTO>> usoSalas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(reporteService.usoSalas(desde, hasta));
    }

    @GetMapping("/uso-salas/horarios")
    public ResponseEntity<List<HorarioUsoDTO>> usoHorarios(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(reporteService.usoHorarios(desde, hasta));
    }

    @GetMapping("/prestamos-por-mes")
    public ResponseEntity<List<PrestamoMesDTO>> prestamosPorMes(@RequestParam(defaultValue = "12") int meses) {
        return ResponseEntity.ok(reporteService.prestamosPorMes(meses));
    }
}
