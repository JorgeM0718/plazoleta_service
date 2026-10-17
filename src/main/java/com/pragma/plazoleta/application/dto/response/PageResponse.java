package com.pragma.plazoleta.application.dto.response;

import java.util.List;

public record PageResponse<T>(List<T> contenido, int pagina, int tamano, long totalElementos, int totalPaginas) {
}
