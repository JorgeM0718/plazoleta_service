package com.pragma.plazoleta.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RestauranteRequest(
        @Schema(example = "El Corral 24") @NotBlank(message = "El nombre es obligatorio") String nombre,
        @Schema(example = "900123456") @NotBlank(message = "El NIT es obligatorio") String nit,
        @Schema(example = "Calle 10 # 5-20, Local 3") @NotBlank(message = "La dirección es obligatoria") String direccion,
        @Schema(example = "+573005698325") @NotBlank(message = "El teléfono es obligatorio") String telefono,
        @Schema(example = "https://cdn.ejemplo.com/logos/elcorral.png") @NotBlank(message = "La URL del logo es obligatoria") String urlLogo,
        @Schema(example = "2", description = "Id de un usuario con rol PROPIETARIO") @NotNull(message = "El id del propietario es obligatorio") Long idPropietario
) {
}
