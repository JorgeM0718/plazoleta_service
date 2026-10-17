package com.pragma.plazoleta.application.dto.response;

public record RestauranteResponse(
        Long id,
        String nombre,
        String nit,
        String direccion,
        String telefono,
        String urlLogo,
        Long idPropietario
) {
}
