package Crai.crai.controller;

import Crai.crai.dto.UsuarioDTO;
import Crai.crai.exception.ResourceNotFoundException;
import Crai.crai.services.IUsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {
    private final IUsuarioService usuarioService;

    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodo());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtener(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id:" + id));
    }

    // Habilita o deshabilita el acceso al CRAI y a los prestamos
    @PutMapping("/{id}/acceso")
    public ResponseEntity<UsuarioDTO> cambiarAcceso(@PathVariable Long id, @RequestParam boolean habilitado) {
        return usuarioService.cambiarAcceso(id, habilitado)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id:" + id));
    }
}
