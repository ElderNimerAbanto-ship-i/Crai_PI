package Crai.crai.services;

import Crai.crai.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface IReporteService {
    ResumenDTO resumen();
    List<LibroPrestadoDTO> librosMasPrestados(int limite);

    // Dashboard. Si no se envian fechas: ultimos 30 dias
    List<UsoEscuelaDTO> usoPorEscuela(LocalDate desde, LocalDate hasta);
    List<UsoSalaDTO> usoSalas(LocalDate desde, LocalDate hasta);
    List<HorarioUsoDTO> usoHorarios(LocalDate desde, LocalDate hasta);
    List<PrestamoMesDTO> prestamosPorMes(int meses);
}
