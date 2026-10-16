package com.machry.machryapi.dto.processamento;

import com.machry.machryapi.dto.etapa.EtapaProcessamentoRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProcessamentoRequestDTO(
        @NotNull(message = "O produto é obrigatório")
        Long produtoId,

        @NotNull(message = "A ficha técnica é obrigatória")
        Long fichaTecnicaId,

        @NotNull(message = "A quantidade produzida é obrigatória")
        @Positive(message = "A quantidade produzida deve ser maior que zero")
        BigDecimal quantidadeProduzida,

        @NotNull(message = "A data do processamento é obrigatória")
        LocalDate dataProcessamento,

        @NotEmpty(message = "O processamento deve possuir pelo menos um item utilizado")
        List<@Valid ItemProcessamentoRequestDTO> itensUtilizados,

        @NotEmpty(message = "O processamento deve possuir pelo menos uma etapa realizada")
        List<@Valid EtapaProcessamentoRequestDTO> etapasRealizadas
) {
}
