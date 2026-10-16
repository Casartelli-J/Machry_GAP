package com.machry.machryapi.model;

import com.machry.machryapi.dto.lote.LoteRequestDTO;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lote")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private BigDecimal quantidade;

    @Column(nullable = false)
    private LocalDate dataProducao;

    @Column(nullable = false)
    private LocalDate dataEntrada;

    @Column(nullable = false)
    private LocalDate dataValidade;

    public Lote() {
    }

    public Lote(LoteRequestDTO request, Item item) {
        this.item = item;
        this.quantidade = request.quantidade();
        this.dataProducao = request.dataProducao();
        this.dataEntrada = request.dataEntrada();
        this.dataValidade = request.dataValidade();
    }

    public void update(LoteRequestDTO request, Item item) {
        this.item = item;
        this.quantidade = request.quantidade();
        this.dataProducao = request.dataProducao();
        this.dataEntrada = request.dataEntrada();
        this.dataValidade = request.dataValidade();
    }

    public Long getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public LocalDate getDataProducao() {
        return dataProducao;
    }

    public void setDataProducao(LocalDate dataProducao) {
        this.dataProducao = dataProducao;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }
}
