package com.pragma.plazoleta.infrastructure.output.jpa.mapper;

import com.pragma.plazoleta.domain.model.Restaurante;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.RestauranteEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IRestauranteEntityMapper {

    RestauranteEntity toEntity(Restaurante restaurante);

    Restaurante toModel(RestauranteEntity entity);
}
