package com.pragma.plazoleta.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PlatoRequest(
        @Schema(example = "Bandeja paisa") @NotBlank(message = "El nombre del plato es obligatorio") String nombre,
        @Schema(example = "32000", description = "Entero positivo mayor a 0")
        @NotNull(message = "El precio es obligatorio") @Positive(message = "El precio debe ser mayor a 0") Integer precio,
        @Schema(example = "Frijoles, arroz, chicharrón, huevo, chorizo y arepa") @NotBlank(message = "La descripción es obligatoria") String descripcion,
        @Schema(example = "https://cdn.ejemplo.com/platos/bandeja.jpg") @NotBlank(message = "La URL de la imagen es obligatoria") String urlImagen,
        @Schema(example = "2") @NotNull(message = "La categoría es obligatoria") Long idCategoria,
        @Schema(example = "1") @NotNull(message = "El restaurante es obligatorio") Long idRestaurante
) {
}
