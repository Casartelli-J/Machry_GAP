package com.machry.machryapi.auth;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {
    public record Sessao(Long usuarioId, String email, Role role, Instant expiraEm) {}
    private final Map<String, Sessao> tokens = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    public String criar(Usuario usuario) {
        byte[] bytes = new byte[32]; random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        tokens.put(token, new Sessao(usuario.getId(), usuario.getEmail(), usuario.getRole(), Instant.now().plusSeconds(8 * 60 * 60)));
        return token;
    }
    public Sessao validar(String token) {
        Sessao sessao = tokens.get(token);
        if (sessao != null && Instant.now().isAfter(sessao.expiraEm())) { tokens.remove(token); return null; }
        return sessao;
    }
    public void remover(String token) { tokens.remove(token); }
}
