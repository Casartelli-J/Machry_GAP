package com.machry.machryapi.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final TokenService tokens;
    public AuthController(UsuarioRepository usuarios, PasswordEncoder encoder, TokenService tokens) { this.usuarios=usuarios; this.encoder=encoder; this.tokens=tokens; }

    public record LoginRequest(@Email @NotBlank String email, @NotBlank String senha) {}
    public record UsuarioResponse(Long id, String nome, String email, String role) {}
    public record LoginResponse(String token, UsuarioResponse usuario) {}

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarios.findByEmailIgnoreCase(request.email().trim()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos."));
        if (!usuario.isAtivo() || !encoder.matches(request.senha(), usuario.getSenha())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
        return new LoginResponse(tokens.criar(usuario), response(usuario));
    }
    @GetMapping("/me")
    public UsuarioResponse me(org.springframework.security.core.Authentication auth) {
        return response(usuarios.findByEmailIgnoreCase(auth.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED)));
    }
    @PostMapping("/logout")
    public Map<String,String> logout(@RequestHeader(value="Authorization", required=false) String header) {
        if (header != null && header.startsWith("Bearer ")) tokens.remover(header.substring(7));
        return Map.of("message", "Sessão encerrada.");
    }
    private UsuarioResponse response(Usuario u) { return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getRole().name()); }
}
