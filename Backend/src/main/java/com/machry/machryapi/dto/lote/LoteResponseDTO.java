package com.machry.machryapi.dto.lote;

import com.machry.machryapi.model.Lote;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoteResponseDTO(
        Long id,
        Long itemId,
        String itemNome,
        BigDecimal quantidade,
        LocalDate dataProducao,
        LocalDate dataEntrada,
        LocalDate dataValidade
) {
    public LoteResponseDTO(Lote lote) {
        this(
                lote.getId(),
                lote.getItem().getId(),
                lote.getItem().getNome(),
                lote.getQuantidade(),
                lote.getDataProducao(),
                lote.getDataEntrada(),
                lote.getDataValidade()
        );
    }
}
