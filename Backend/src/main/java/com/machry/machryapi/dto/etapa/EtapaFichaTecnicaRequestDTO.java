package com.machry.machryapi.dto.etapa;

import com.machry.machryapi.model.enums.TipoMetodo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EtapaFichaTecnicaRequestDTO(
        @NotNull(message = "A ordem da etapa é obrigatória")
        @Positive(message = "A ordem da etapa deve ser maior que zero")
        Integer ordem,

        @NotBlank(message = "O nome da etapa é obrigatório")
        String nome,

        @NotNull(message = "O tipo de método é obrigatório")
        TipoMetodo tipoMetodo,

        String descricao,

        @NotNull(message = "O tempo previsto é obrigatório")
        @Positive(message = "O tempo previsto deve ser maior que zero")
        Integer tempoPrevistoMinutos
) {
}
