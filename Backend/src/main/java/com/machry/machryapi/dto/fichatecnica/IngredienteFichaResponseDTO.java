package com.machry.machryapi.dto.fichatecnica;

import com.machry.machryapi.model.IngredienteFicha;

import java.math.BigDecimal;

public record IngredienteFichaResponseDTO(
        Long id,
        Long itemId,
        String itemNome,
        BigDecimal quantidade,
        String unidadeMedida
) {
    public IngredienteFichaResponseDTO(IngredienteFicha ingrediente) {
        this(
                ingrediente.getId(),
                ingrediente.getItem().getId(),
                ingrediente.getItem().getNome(),
                ingrediente.getQuantidade(),
                ingrediente.getItem().getUnidadeMedida()
        );
    }
}
