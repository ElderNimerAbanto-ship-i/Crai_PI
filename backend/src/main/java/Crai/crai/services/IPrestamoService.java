package Crai.crai.services;

import Crai.crai.dto.DetallePrestamoDTO;
import Crai.crai.dto.PrestamoDTO;
import Crai.crai.dto.PrestamoProcedimientoDTO;
import Crai.crai.dto.PrestamoRegistroDTO;

import java.util.List;
import java.util.Optional;

public interface IPrestamoService {
    // Registro 100% en Oracle: llama directo a CRAI.sp_registrar_prestamo
    PrestamoDTO registrarConProcedimiento(PrestamoProcedimientoDTO dto);
    //PrestamoDTO registrar(PrestamoRegistroDTO dto);
    PrestamoDTO devolver(Long idEjemplar);
    PrestamoDTO renovar(Long idPrestamo, int diasExtension);

    List<PrestamoDTO> listarTodo();
    List<PrestamoDTO> listarActivos();
    List<PrestamoDTO> listarVencidos();
    List<PrestamoDTO> listarPorPersona(Long idPersona);

    Optional<PrestamoDTO> buscarPorId(Long id);
    List<DetallePrestamoDTO> listarDetalle(Long idPrestamo);
}
