package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.RestauranteRequest;
import com.pragma.plazoleta.application.dto.response.PageResponse;
import com.pragma.plazoleta.application.dto.response.RestauranteResponse;
import com.pragma.plazoleta.application.dto.response.RestauranteResumenResponse;
import com.pragma.plazoleta.application.mapper.IRestauranteDtoMapper;
import com.pragma.plazoleta.domain.api.IRestauranteServicePort;
import com.pragma.plazoleta.domain.model.PageResult;
import com.pragma.plazoleta.domain.model.Restaurante;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestauranteHandler {

    private final IRestauranteServicePort restauranteServicePort;
    private final IRestauranteDtoMapper mapper;

    public RestauranteResponse crearRestaurante(RestauranteRequest request) {
        return mapper.toResponse(restauranteServicePort.crearRestaurante(mapper.toModel(request)));
    }

    public PageResponse<RestauranteResumenResponse> listarRestaurantes(int pagina, int tamano) {
        PageResult<Restaurante> resultado = restauranteServicePort.listarRestaurantes(pagina, tamano);
        return new PageResponse<>(
                resultado.contenido().stream().map(mapper::toResumen).toList(),
                resultado.pagina(),
                resultado.tamano(),
                resultado.totalElementos(),
                resultado.totalPaginas());
    }
}
