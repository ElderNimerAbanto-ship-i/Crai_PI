package Crai.crai.controller;

import Crai.crai.dto.AuthResponse;
import Crai.crai.dto.LoginRequest;
import Crai.crai.dto.UsuarioDTO;
import Crai.crai.dto.UsuarioRegistroDTO;
import Crai.crai.entity.Usuario;
import Crai.crai.repository.UsuarioRepository;
import Crai.crai.security.JwtService;
import Crai.crai.security.UsuarioPrincipal;
import Crai.crai.services.IUsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final IUsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthController(IUsuarioService usuarioService, AuthenticationManager authenticationManager,
                          UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    // Registro combinado persona + usuario
    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registro(@Valid @RequestBody UsuarioRegistroDTO dto) {
        return new ResponseEntity<>(usuarioService.registrar(dto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        // si el usuario o la clave estan mal, lanza BadCredentialsException (401)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        Usuario user = usuarioRepository.findByUsername(request.username()).orElseThrow();
        String jwtToken = jwtService.generateToken(new UsuarioPrincipal(user));

        return ResponseEntity.ok(new AuthResponse(
                jwtToken,
                user.getUsername(),
                user.getRol().name(),
                user.getIdPersona(),
                user.getPersona().getNombres() + " " + user.getPersona().getApellidos()
        ));
    }
}