package com.machry.machryapi.model;

import com.machry.machryapi.dto.processamento.ProcessamentoRequestDTO;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "processamento")
public class Processamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Item produto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ficha_tecnica_id", nullable = false)
    private FichaTecnica fichaTecnica;

    @Column(nullable = false)
    private BigDecimal quantidadeProduzida;

    @Column(nullable = false)
    private LocalDate dataProcessamento;

    @OneToMany(
            mappedBy = "processamento",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemProcessamento> itensUtilizados = new ArrayList<>();

    public Processamento() {
    }

    public Processamento(
            ProcessamentoRequestDTO request,
            Item produto,
            FichaTecnica fichaTecnica
    ) {
        this.produto = produto;
        this.fichaTecnica = fichaTecnica;
        this.quantidadeProduzida = request.quantidadeProduzida();
        this.dataProcessamento = request.dataProcessamento();
    }

    public void update(
            ProcessamentoRequestDTO request,
            Item produto,
            FichaTecnica fichaTecnica
    ) {
        this.produto = produto;
        this.fichaTecnica = fichaTecnica;
        this.quantidadeProduzida = request.quantidadeProduzida();
        this.dataProcessamento = request.dataProcessamento();
    }

    public void replaceItensUtilizados(List<ItemProcessamento> novosItens) {
        this.itensUtilizados.clear();
        novosItens.forEach(this::addItemUtilizado);
    }

    public void addItemUtilizado(ItemProcessamento itemProcessamento) {
        itemProcessamento.setProcessamento(this);
        this.itensUtilizados.add(itemProcessamento);
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

    public FichaTecnica getFichaTecnica() {
        return fichaTecnica;
    }

    public void setFichaTecnica(FichaTecnica fichaTecnica) {
        this.fichaTecnica = fichaTecnica;
    }

    public BigDecimal getQuantidadeProduzida() {
        return quantidadeProduzida;
    }

    public void setQuantidadeProduzida(BigDecimal quantidadeProduzida) {
        this.quantidadeProduzida = quantidadeProduzida;
    }

    public LocalDate getDataProcessamento() {
        return dataProcessamento;
    }

    public void setDataProcessamento(LocalDate dataProcessamento) {
        this.dataProcessamento = dataProcessamento;
    }

    public List<ItemProcessamento> getItensUtilizados() {
        return itensUtilizados;
    }
}
