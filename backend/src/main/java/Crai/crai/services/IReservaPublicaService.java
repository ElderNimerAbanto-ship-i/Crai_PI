package Crai.crai.services;

import Crai.crai.dto.*;

import java.util.List;

public interface IReservaPublicaService {
    // Paso 1: validar a cada estudiante del grupo
    EstudianteValidadoDTO validarEstudiante(String codigoUni);

    // Paso 2: consultar que sala se asignaria (hoy, desde la hora actual)
    DisponibilidadDTO consultarDisponibilidad(int duracionMinutos, int cantidadPersonas);

    // Confirmar: vuelve a validar todo y registra la reserva con sus integrantes
    ReservaPublicaResultadoDTO reservar(ReservaPublicaDTO dto);

    // Pantalla "Ver salas": estado actual de cada sala
    List<SalaEstadoDTO> estadoSalas();

    // Horarios de hoy de una sala segun la duracion elegida (libres, ocupados, ya iniciados)
    List<HorarioSalaDTO> horariosSala(Long idSala, int duracionMinutos);
}
