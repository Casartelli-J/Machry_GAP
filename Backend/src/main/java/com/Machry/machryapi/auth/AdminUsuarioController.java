package com.machry.machryapi.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminUsuarioController {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    public AdminUsuarioController(UsuarioRepository usuarios, PasswordEncoder encoder) { this.usuarios=usuarios; this.encoder=encoder; }
    public record CriarUsuarioRequest(@NotBlank @Size(max=100) String nome, @NotBlank @Email String email, @NotBlank @Size(min=8, max=100) String senha, @NotNull Role role) {}
    public record UsuarioView(Long id, String nome, String email, String role, boolean ativo) {}
    @GetMapping public List<UsuarioView> listar() { return usuarios.findAll().stream().map(this::view).toList(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public UsuarioView criar(@Valid @RequestBody CriarUsuarioRequest req) {
        if (usuarios.existsByEmailIgnoreCase(req.email().trim())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe usuário com esse e-mail.");
        Usuario u = usuarios.save(new Usuario(req.nome().trim(), req.email().trim().toLowerCase(), encoder.encode(req.senha()), req.role()));
        return view(u);
    }
    @PatchMapping("/{id}/status") public UsuarioView status(@PathVariable Long id, @RequestBody StatusRequest req) {
        Usuario u=usuarios.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
        u.setAtivo(req.ativo()); return view(usuarios.save(u));
    }
    public record StatusRequest(boolean ativo) {}
    private UsuarioView view(Usuario u) { return new UsuarioView(u.getId(),u.getNome(),u.getEmail(),u.getRole().name(),u.isAtivo()); }
}
