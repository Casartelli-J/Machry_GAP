package com.machry.machryapi.model;

import com.machry.machryapi.dto.item.ItemRequestDTO;
import com.machry.machryapi.model.enums.TipoItem;
import jakarta.persistence.*;

@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoItem tipo;

    @Column(nullable = false)
    private String unidadeMedida;

    public Item() {
    }

    public Item(ItemRequestDTO request) {
        this.nome = request.nome();
        this.tipo = request.tipo();
        this.unidadeMedida = request.unidadeMedida();
    }

    public void update(ItemRequestDTO request) {
        this.nome = request.nome();
        this.tipo = request.tipo();
        this.unidadeMedida = request.unidadeMedida();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoItem getTipo() {
        return tipo;
    }

    public void setTipo(TipoItem tipo) {
        this.tipo = tipo;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }
}
