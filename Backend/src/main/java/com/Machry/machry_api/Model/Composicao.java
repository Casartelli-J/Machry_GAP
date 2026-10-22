package com.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "composicao")
public class Composicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idComposicao;

    private String ingrediente;
    private Double quantidade;
    private String unidadeMedida;
    @ManyToOne
    @JoinColumn(name = "id_ficha")
    @JsonIgnore // Evita loop infinito na hora de mostrar o resultado na tela
    private FichaTecnica fichaTecnica;
    public Integer getIdComposicao() { return idComposicao; }
    public void setIdComposicao(Integer idComposicao) { this.idComposicao = idComposicao; }

    public String getIngrediente() { return ingrediente; }
    public void setIngrediente(String ingrediente) { this.ingrediente = ingrediente; }

    public Double getQuantidade() { return quantidade; }
    public void setQuantidade(Double quantidade) { this.quantidade = quantidade; }

    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    public FichaTecnica getFichaTecnica() { return fichaTecnica; }
    public void setFichaTecnica(FichaTecnica fichaTecnica) { this.fichaTecnica = fichaTecnica; }
}
