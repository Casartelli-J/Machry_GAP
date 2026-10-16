package com.machry.machryapi.dto.item;

import com.machry.machryapi.model.Item;
import com.machry.machryapi.model.enums.TipoItem;

public record ItemResponseDTO(
        Long id,
        String nome,
        TipoItem tipo,
        String unidadeMedida
) {
    public ItemResponseDTO(Item item) {
        this(
                item.getId(),
                item.getNome(),
                item.getTipo(),
                item.getUnidadeMedida()
        );
    }
}
