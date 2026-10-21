package com.machry.machryapi.dto.etapa;

import com.machry.machryapi.model.EtapaFichaTecnica;
import com.machry.machryapi.model.enums.TipoMetodo;

public record EtapaFichaTecnicaResponseDTO(
        Long id,
        Integer ordem,
        String nome,
        TipoMetodo tipoMetodo,
        String descricao,
        Integer tempoPrevistoMinutos
) {
    public EtapaFichaTecnicaResponseDTO(EtapaFichaTecnica etapa) {
        this(
                etapa.getId(),
                etapa.getOrdem(),
                etapa.getNome(),
                etapa.getTipoMetodo(),
                etapa.getDescricao(),
                etapa.getTempoPrevistoMinutos()
        );
    }
}
