package com.pragma.plazoleta.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PlatoEstadoRequest(
        @Schema(example = "false") @NotNull(message = "El campo activo es obligatorio") Boolean activo
) {
}
