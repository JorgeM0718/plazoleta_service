package com.pragma.plazoleta.infrastructure.output.jpa.mapper;

import com.pragma.plazoleta.domain.model.Plato;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.CategoriaEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.PlatoEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.RestauranteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface IPlatoEntityMapper {

    @Mapping(target = "restaurante", source = "idRestaurante", qualifiedByName = "restauranteRef")
    @Mapping(target = "categoria", source = "idCategoria", qualifiedByName = "categoriaRef")
    PlatoEntity toEntity(Plato plato);

    @Mapping(target = "idRestaurante", source = "restaurante.id")
    @Mapping(target = "idCategoria", source = "categoria.id")
    Plato toModel(PlatoEntity entity);

    @Named("restauranteRef")
    default RestauranteEntity restauranteRef(Long id) {
        return id == null ? null : RestauranteEntity.builder().id(id).build();
    }

    @Named("categoriaRef")
    default CategoriaEntity categoriaRef(Long id) {
        return id == null ? null : CategoriaEntity.builder().id(id).build();
    }
}
