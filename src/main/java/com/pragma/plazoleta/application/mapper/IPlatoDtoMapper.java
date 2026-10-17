package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.PlatoRequest;
import com.pragma.plazoleta.application.dto.response.PlatoResponse;
import com.pragma.plazoleta.domain.model.Plato;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IPlatoDtoMapper {

    Plato toModel(PlatoRequest request);

    PlatoResponse toResponse(Plato plato);
}
