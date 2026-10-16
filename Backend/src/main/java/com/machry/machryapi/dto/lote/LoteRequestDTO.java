package com.machry.machryapi.dto.lote;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoteRequestDTO(
        @NotNull(message = "O item é obrigatório")
        Long itemId,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        BigDecimal quantidade,

        @NotNull(message = "A data de produção é obrigatória")
        LocalDate dataProducao,

        @NotNull(message = "A data de entrada é obrigatória")
        LocalDate dataEntrada,

        @NotNull(message = "A data de validade é obrigatória")
        LocalDate dataValidade
) {
}
