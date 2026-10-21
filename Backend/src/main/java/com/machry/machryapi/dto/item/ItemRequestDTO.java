package com.machry.machryapi.dto.item;

import com.machry.machryapi.model.enums.TipoItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotNull(message = "O tipo é obrigatório")
        TipoItem tipo,

        @NotBlank(message = "A unidade de medida é obrigatória")
        String unidadeMedida
) {
}
