package Crai.crai.controller;

import Crai.crai.dto.LibroDTO;
import Crai.crai.exception.ResourceNotFoundException;
import Crai.crai.services.IFileStorageService;
import Crai.crai.services.ILibroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/libros")
public class LibroController {
    private final ILibroService libroService;
    private final IFileStorageService fileStorageService;

    public LibroController(ILibroService libroService, IFileStorageService fileStorageService) {
        this.libroService = libroService;
        this.fileStorageService = fileStorageService;
    }

    // ?buscar=texto filtra por titulo o autor
    @GetMapping
    public ResponseEntity<List<LibroDTO>> listar(@RequestParam(required = false) String buscar) {
        return ResponseEntity.ok(libroService.listarTodo(buscar));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibroDTO> obtener(@PathVariable Long id) {
        return libroService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id:" + id));
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<LibroDTO> crear(
            @ModelAttribute @org.springdoc.core.annotations.ParameterObject @Valid LibroDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        String nombreImagen = null;
        if (file != null && !file.isEmpty()) {
            nombreImagen = fileStorageService.store(file);
        }

        // Como los Records son inmutables, creamos uno nuevo con el nombre de la imagen
        LibroDTO libroConImagen = new LibroDTO(
                null,
                dto.titulo(),
                dto.autor(),
                dto.seccionEspecial(),
                dto.idCategoria(),
                null,
                nombreImagen,
                null
        );

        try {
            return new ResponseEntity<>(libroService.crear(libroConImagen), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Si falla guardar el libro, no dejamos la imagen huérfana
            if (nombreImagen != null) {
                fileStorageService.delete(nombreImagen);
            }
            throw e;
        }
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<LibroDTO> actualizar(
            @PathVariable Long id,
            @ModelAttribute @org.springdoc.core.annotations.ParameterObject @Valid LibroDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        // 1. Obtener el libro actual para saber qué imagen tiene
        LibroDTO libroActual = libroService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con id:" + id));

        // Por defecto mantenemos la imagen actual (el DTO trae la ruta relativa, no el nombre del archivo)
        String nombreImagen = nombreArchivo(libroActual.imagen());
        String imagenNueva = null;

        // 2. Si el usuario subió un nuevo archivo, lo guardamos
        if (file != null && !file.isEmpty()) {
            imagenNueva = fileStorageService.store(file);
            nombreImagen = imagenNueva;
        }

        // 3. Creamos el DTO final con el nombre de la imagen (nueva o vieja)
        LibroDTO dtoParaActualizar = new LibroDTO(
                id, dto.titulo(), dto.autor(), dto.seccionEspecial(), dto.idCategoria(), null, nombreImagen, null
        );

        try {
            return libroService.actualizar(id, dtoParaActualizar)
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> new ResourceNotFoundException("No se pudo actualizar con id:" + id));
        } catch (RuntimeException e) {
            if (imagenNueva != null) {
                fileStorageService.delete(imagenNueva);
            }
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (libroService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        throw new ResourceNotFoundException("No se pudo eliminar con id:" + id);
    }

    // De "/crai/images/uuid_foto.jpg" obtiene "uuid_foto.jpg"
    private String nombreArchivo(String rutaRelativa) {
        if (rutaRelativa == null) return null;
        return rutaRelativa.substring(rutaRelativa.lastIndexOf('/') + 1);
    }
}
