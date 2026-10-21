package com.machry.machryapi.dto.etapa;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EtapaProcessamentoRequestDTO(
        @NotNull(message = "A etapa da ficha técnica é obrigatória")
        Long etapaFichaTecnicaId,

        @NotNull(message = "O tempo realizado é obrigatório")
        @Positive(message = "O tempo realizado deve ser maior que zero")
        Integer tempoRealizadoMinutos,

        String observacao
) {
}
