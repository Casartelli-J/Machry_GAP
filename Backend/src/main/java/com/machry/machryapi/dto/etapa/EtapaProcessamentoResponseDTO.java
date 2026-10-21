package com.machry.machryapi.dto.etapa;

import com.machry.machryapi.model.EtapaProcessamento;
import com.machry.machryapi.model.enums.TipoMetodo;

public record EtapaProcessamentoResponseDTO(
        Long id,
        Long etapaFichaTecnicaId,
        Integer ordem,
        String nome,
        TipoMetodo tipoMetodo,
        Integer tempoPrevistoMinutos,
        Integer tempoRealizadoMinutos,
        String observacao
) {
    public EtapaProcessamentoResponseDTO(EtapaProcessamento etapa) {
        this(
                etapa.getId(),
                etapa.getEtapaFichaTecnicaIdOrigem(),
                etapa.getOrdem(),
                etapa.getNome(),
                etapa.getTipoMetodo(),
                etapa.getTempoPrevistoMinutos(),
                etapa.getTempoRealizadoMinutos(),
                etapa.getObservacao()
        );
    }
}
