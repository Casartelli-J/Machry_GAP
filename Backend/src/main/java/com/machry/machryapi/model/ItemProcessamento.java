package com.machry.machryapi.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "item_processamento")
public class ItemProcessamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "processamento_id", nullable = false)
    private Processamento processamento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private BigDecimal quantidadeUtilizada;

    public ItemProcessamento() {
    }

    public ItemProcessamento(Item item, BigDecimal quantidadeUtilizada) {
        this.item = item;
        this.quantidadeUtilizada = quantidadeUtilizada;
    }

    public Long getId() {
        return id;
    }

    public Processamento getProcessamento() {
        return processamento;
    }

    public void setProcessamento(Processamento processamento) {
        this.processamento = processamento;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public BigDecimal getQuantidadeUtilizada() {
        return quantidadeUtilizada;
    }

    public void setQuantidadeUtilizada(BigDecimal quantidadeUtilizada) {
        this.quantidadeUtilizada = quantidadeUtilizada;
    }
}
