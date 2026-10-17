package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.domain.model.PageResult;
import com.pragma.plazoleta.domain.model.Restaurante;
import com.pragma.plazoleta.domain.spi.IRestaurantePersistencePort;
import com.pragma.plazoleta.infrastructure.output.jpa.entity.RestauranteEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.IRestauranteEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IRestauranteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public class RestauranteJpaAdapter implements IRestaurantePersistencePort {

    private final IRestauranteRepository repository;
    private final IRestauranteEntityMapper mapper;

    public RestauranteJpaAdapter(IRestauranteRepository repository, IRestauranteEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Restaurante guardar(Restaurante restaurante) {
        return mapper.toModel(repository.save(mapper.toEntity(restaurante)));
    }

    @Override
    public Optional<Restaurante> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toModel);
    }

    @Override
    public boolean existePorNit(String nit) {
        return repository.existsByNit(nit);
    }

    @Override
    public PageResult<Restaurante> listarOrdenadosPorNombre(int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina, tamano, Sort.by(Sort.Order.asc("nombre").ignoreCase()));
        Page<RestauranteEntity> resultado = repository.findAll(pageable);
        return new PageResult<>(
                resultado.getContent().stream().map(mapper::toModel).toList(),
                resultado.getNumber(),
                resultado.getSize(),
                resultado.getTotalElements(),
                resultado.getTotalPages());
    }
}
