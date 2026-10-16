package com.machry.machryapi.dto.processamento;

import com.machry.machryapi.model.ItemProcessamento;

import java.math.BigDecimal;

public record ItemProcessamentoResponseDTO(
        Long id,
        Long itemId,
        String itemNome,
        BigDecimal quantidadeUtilizada,
        String unidadeMedida
) {
    public ItemProcessamentoResponseDTO(ItemProcessamento itemProcessamento) {
        this(
                itemProcessamento.getId(),
                itemProcessamento.getItem().getId(),
                itemProcessamento.getItem().getNome(),
                itemProcessamento.getQuantidadeUtilizada(),
                itemProcessamento.getItem().getUnidadeMedida()
        );
    }
}
