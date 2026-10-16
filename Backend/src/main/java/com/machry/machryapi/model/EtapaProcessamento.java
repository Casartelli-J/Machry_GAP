package com.machry.machryapi.model;

import com.machry.machryapi.dto.etapa.EtapaProcessamentoRequestDTO;
import com.machry.machryapi.model.enums.TipoMetodo;
import jakarta.persistence.*;

@Entity
@Table(name = "etapa_processamento")
public class EtapaProcessamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "processamento_id", nullable = false)
    private Processamento processamento;

    /*
     * ID da etapa planejada que originou este registro.
     * Não é uma FK propositalmente: o processamento mantém um snapshot histórico
     * mesmo que a ficha técnica seja alterada posteriormente.
     */
    @Column(nullable = false)
    private Long etapaFichaTecnicaIdOrigem;

    @Column(nullable = false)
    private Integer ordem;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMetodo tipoMetodo;

    @Column(nullable = false)
    private Integer tempoPrevistoMinutos;

    @Column(nullable = false)
    private Integer tempoRealizadoMinutos;

    private String observacao;

    public EtapaProcessamento() {
    }

    public EtapaProcessamento(
            EtapaFichaTecnica etapaPlanejada,
            EtapaProcessamentoRequestDTO request
    ) {
        this.etapaFichaTecnicaIdOrigem = etapaPlanejada.getId();
        this.ordem = etapaPlanejada.getOrdem();
        this.nome = etapaPlanejada.getNome();
        this.tipoMetodo = etapaPlanejada.getTipoMetodo();
        this.tempoPrevistoMinutos = etapaPlanejada.getTempoPrevistoMinutos();
        this.tempoRealizadoMinutos = request.tempoRealizadoMinutos();
        this.observacao = request.observacao();
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

    public Long getEtapaFichaTecnicaIdOrigem() {
        return etapaFichaTecnicaIdOrigem;
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

    public Integer getTempoPrevistoMinutos() {
        return tempoPrevistoMinutos;
    }

    public Integer getTempoRealizadoMinutos() {
        return tempoRealizadoMinutos;
    }

    public String getObservacao() {
        return observacao;
    }
}
