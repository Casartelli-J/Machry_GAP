package com.machry.machryapi.model;

import com.machry.machryapi.dto.etapa.EtapaFichaTecnicaRequestDTO;
import com.machry.machryapi.model.enums.TipoMetodo;
import jakarta.persistence.*;

@Entity
@Table(name = "etapa_ficha_tecnica")
public class EtapaFichaTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ficha_tecnica_id", nullable = false)
    private FichaTecnica fichaTecnica;

    @Column(nullable = false)
    private Integer ordem;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMetodo tipoMetodo;

    private String descricao;

    @Column(nullable = false)
    private Integer tempoPrevistoMinutos;

    public EtapaFichaTecnica() {
    }

    public EtapaFichaTecnica(EtapaFichaTecnicaRequestDTO request) {
        this.ordem = request.ordem();
        this.nome = request.nome();
        this.tipoMetodo = request.tipoMetodo();
        this.descricao = request.descricao();
        this.tempoPrevistoMinutos = request.tempoPrevistoMinutos();
    }

    public Long getId() {
        return id;
    }

    public FichaTecnica getFichaTecnica() {
        return fichaTecnica;
    }

    public void setFichaTecnica(FichaTecnica fichaTecnica) {
        this.fichaTecnica = fichaTecnica;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public String getNome() {
        return nome;
    }

    public TipoMetodo getTipoMetodo() {
        return tipoMetodo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getTempoPrevistoMinutos() {
        return tempoPrevistoMinutos;
    }
}
