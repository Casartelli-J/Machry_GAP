package com.machry.machryapi.dto.fichatecnica;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record IngredienteFichaRequestDTO(
        @NotNull(message = "O item do ingrediente é obrigatório")
        Long itemId,

        @NotNull(message = "A quantidade do ingrediente é obrigatória")
        @Positive(message = "A quantidade do ingrediente deve ser maior que zero")
        BigDecimal quantidade
) {
}
