package com.pragma.plazoleta.domain.model;

import java.util.List;

/** Resultado paginado independiente de Spring Data (el dominio no conoce frameworks). */
public record PageResult<T>(List<T> contenido, int pagina, int tamano, long totalElementos, int totalPaginas) {
}
