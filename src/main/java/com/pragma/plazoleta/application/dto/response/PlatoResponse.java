package com.pragma.plazoleta.application.dto.response;

public record PlatoResponse(
        Long id,
        String nombre,
        Integer precio,
        String descripcion,
        String urlImagen,
        Boolean activo,
        Long idRestaurante,
        Long idCategoria
) {
}
