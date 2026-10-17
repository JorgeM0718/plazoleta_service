package com.pragma.plazoleta.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

/** Solo se puede modificar precio y descripción. Al menos uno debe venir. */
public record PlatoUpdateRequest(
        @Schema(example = "35000", nullable = true) @Positive(message = "El precio debe ser mayor a 0") Integer precio,
        @Schema(example = "Ahora con aguacate", nullable = true) String descripcion
) {
}
