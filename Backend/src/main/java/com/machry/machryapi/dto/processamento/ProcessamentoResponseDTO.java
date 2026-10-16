package com.machry.machryapi.dto.processamento;

import com.machry.machryapi.dto.etapa.EtapaProcessamentoResponseDTO;
import com.machry.machryapi.model.Processamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProcessamentoResponseDTO(
        Long id,
        Long produtoId,
        String produtoNome,
        Long fichaTecnicaId,
        BigDecimal quantidadeProduzida,
        LocalDate dataProcessamento,
        List<ItemProcessamentoResponseDTO> itensUtilizados,
        List<EtapaProcessamentoResponseDTO> etapasRealizadas
) {
    public ProcessamentoResponseDTO(Processamento processamento) {
        this(
                processamento.getId(),
                processamento.getProduto().getId(),
                processamento.getProduto().getNome(),
                processamento.getFichaTecnica().getId(),
                processamento.getQuantidadeProduzida(),
                processamento.getDataProcessamento(),
                processamento.getItensUtilizados()
                        .stream()
                        .map(ItemProcessamentoResponseDTO::new)
                        .toList(),
                processamento.getEtapasRealizadas()
                        .stream()
                        .map(EtapaProcessamentoResponseDTO::new)
                        .toList()
        );
    }
}
