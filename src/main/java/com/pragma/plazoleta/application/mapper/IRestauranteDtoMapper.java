package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.RestauranteRequest;
import com.pragma.plazoleta.application.dto.response.RestauranteResponse;
import com.pragma.plazoleta.application.dto.response.RestauranteResumenResponse;
import com.pragma.plazoleta.domain.model.Restaurante;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IRestauranteDtoMapper {

    Restaurante toModel(RestauranteRequest request);

    RestauranteResponse toResponse(Restaurante restaurante);

    RestauranteResumenResponse toResumen(Restaurante restaurante);
}
