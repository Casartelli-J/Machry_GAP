package com.machry.machryapi.dto.fichatecnica;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record FichaTecnicaRequestDTO(
        @NotNull(message = "O produto é obrigatório")
        Long produtoId,

        @NotNull(message = "O rendimento é obrigatório")
        @Positive(message = "O rendimento deve ser maior que zero")
        BigDecimal rendimento,

        @NotBlank(message = "A unidade de rendimento é obrigatória")
        String unidadeRendimento,

        @NotEmpty(message = "A ficha técnica deve possuir pelo menos um ingrediente")
        List<@Valid IngredienteFichaRequestDTO> ingredientes
) {
}
