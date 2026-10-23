package com.machry.machryapi.auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeed {
    @Bean CommandLineRunner criarAdministradorInicial(UsuarioRepository usuarios, PasswordEncoder encoder) {
        return args -> {
            String email = System.getenv().getOrDefault("MACHRY_ADMIN_EMAIL", "admin@machry.local");
            String senha = System.getenv().getOrDefault("MACHRY_ADMIN_PASSWORD", "Machry@123");
            if (!usuarios.existsByEmailIgnoreCase(email)) usuarios.save(new Usuario("Administrador Geral", email, encoder.encode(senha), Role.ADMIN_GERAL));
        };
    }
}
