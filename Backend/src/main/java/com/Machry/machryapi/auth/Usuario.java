package com.machry.machryapi.auth;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String nome;
    @Column(nullable = false, length = 160, unique = true)
    private String email;
    @Column(nullable = false, length = 100)
    private String senha;
    @Enumerated(EnumType.STRING) @Column(name = "perfil", nullable = false, length = 30)
    private Role role;
    @Column(nullable = false)
    private boolean ativo = true;

    protected Usuario() {}
    public Usuario(String nome, String email, String senha, Role role) {
        this.nome = nome; this.email = email; this.senha = senha; this.role = role; this.ativo = true;
    }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public Role getRole() { return role; }
    public boolean isAtivo() { return ativo; }
    public void setNome(String nome) { this.nome = nome; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setRole(Role role) { this.role = role; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
