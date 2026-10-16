package com.machry.machryapi.model;

import com.machry.machryapi.dto.fichatecnica.FichaTecnicaRequestDTO;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ficha_tecnica")
public class FichaTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false, unique = true)
    private Item produto;

    @Column(nullable = false)
    private BigDecimal rendimento;

    @Column(nullable = false)
    private String unidadeRendimento;

    @OneToMany(
            mappedBy = "fichaTecnica",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<IngredienteFicha> ingredientes = new ArrayList<>();

    @OneToMany(
            mappedBy = "fichaTecnica",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("ordem ASC")
    private List<EtapaFichaTecnica> etapas = new ArrayList<>();

    public FichaTecnica() {
    }

    public FichaTecnica(FichaTecnicaRequestDTO request, Item produto) {
        this.produto = produto;
        this.rendimento = request.rendimento();
        this.unidadeRendimento = request.unidadeRendimento();
    }

    public void update(FichaTecnicaRequestDTO request, Item produto) {
        this.produto = produto;
        this.rendimento = request.rendimento();
        this.unidadeRendimento = request.unidadeRendimento();
    }

    public void replaceIngredientes(List<IngredienteFicha> novosIngredientes) {
        this.ingredientes.clear();
        novosIngredientes.forEach(this::addIngrediente);
    }

    public void addIngrediente(IngredienteFicha ingrediente) {
        ingrediente.setFichaTecnica(this);
        this.ingredientes.add(ingrediente);
    }

    public void replaceEtapas(List<EtapaFichaTecnica> novasEtapas) {
        this.etapas.clear();
        novasEtapas.forEach(this::addEtapa);
    }

    public void addEtapa(EtapaFichaTecnica etapa) {
        etapa.setFichaTecnica(this);
        this.etapas.add(etapa);
    }

    public Long getId() {
        return id;
    }

    public Item getProduto() {
        return produto;
    }

    public void setProduto(Item produto) {
        this.produto = produto;
    }

    public BigDecimal getRendimento() {
        return rendimento;
    }

    public void setRendimento(BigDecimal rendimento) {
        this.rendimento = rendimento;
    }

    public String getUnidadeRendimento() {
        return unidadeRendimento;
    }

    public void setUnidadeRendimento(String unidadeRendimento) {
        this.unidadeRendimento = unidadeRendimento;
    }

    public List<IngredienteFicha> getIngredientes() {
        return ingredientes;
    }

    public List<EtapaFichaTecnica> getEtapas() {
        return etapas;
    }
}
