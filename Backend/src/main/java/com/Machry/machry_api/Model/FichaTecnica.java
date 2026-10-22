package com.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ficha_tecnica")
public class FichaTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFicha;

    private String nome;
    private Double rendimento;
    private String unidadeRendimento;

    @Enumerated(EnumType.STRING)
    private TipoMetodo method;
    @OneToMany(mappedBy = "fichaTecnica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Composicao> ingredientes = new ArrayList<>();
    public Integer getIdFicha() { return idFicha; }
    public void setIdFicha(Integer idFicha) { this.idFicha = idFicha; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Double getRendimento() { return rendimento; }
    public void setRendimento(Double rendimento) { this.rendimento = rendimento; }

    public String getUnidadeRendimento() { return unidadeRendimento; }
    public void setUnidadeRendimento(String unidadeRendimento) { this.unidadeRendimento = unidadeRendimento; }

    public TipoMetodo getMethod() { return method; }
    public void setMethod(TipoMetodo method) { this.method = method; }

    public List<Composicao> getIngredientes() { return ingredientes; }
    public void setIngredientes(List<Composicao> ingredientes) { this.ingredientes = ingredientes; }
}
