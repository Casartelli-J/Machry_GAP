package com.machry.machryapi.dto.fichatecnica;

import com.machry.machryapi.dto.etapa.EtapaFichaTecnicaResponseDTO;
import com.machry.machryapi.model.FichaTecnica;

import java.math.BigDecimal;
import java.util.List;

public record FichaTecnicaResponseDTO(
        Long id,
        Long produtoId,
        String produtoNome,
        BigDecimal rendimento,
        String unidadeRendimento,
        List<IngredienteFichaResponseDTO> ingredientes,
        List<EtapaFichaTecnicaResponseDTO> etapas
) {
    public FichaTecnicaResponseDTO(FichaTecnica fichaTecnica) {
        this(
                fichaTecnica.getId(),
                fichaTecnica.getProduto().getId(),
                fichaTecnica.getProduto().getNome(),
                fichaTecnica.getRendimento(),
                fichaTecnica.getUnidadeRendimento(),
                fichaTecnica.getIngredientes()
                        .stream()
                        .map(IngredienteFichaResponseDTO::new)
                        .toList(),
                fichaTecnica.getEtapas()
                        .stream()
                        .map(EtapaFichaTecnicaResponseDTO::new)
                        .toList()
        );
    }
}
