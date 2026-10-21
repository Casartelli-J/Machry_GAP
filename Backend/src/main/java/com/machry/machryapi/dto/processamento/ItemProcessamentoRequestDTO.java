package com.machry.machryapi.dto.processamento;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ItemProcessamentoRequestDTO(
        @NotNull(message = "O item utilizado é obrigatório")
        Long itemId,

        @NotNull(message = "A quantidade utilizada é obrigatória")
        @Positive(message = "A quantidade utilizada deve ser maior que zero")
        BigDecimal quantidadeUtilizada
) {
}
