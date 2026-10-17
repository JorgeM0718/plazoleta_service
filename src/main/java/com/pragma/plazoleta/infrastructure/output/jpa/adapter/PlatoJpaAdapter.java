package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.domain.model.Plato;
import com.pragma.plazoleta.domain.spi.IPlatoPersistencePort;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.IPlatoEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IPlatoRepository;

import java.util.Optional;

public class PlatoJpaAdapter implements IPlatoPersistencePort {

    private final IPlatoRepository repository;
    private final IPlatoEntityMapper mapper;

    public PlatoJpaAdapter(IPlatoRepository repository, IPlatoEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Plato guardar(Plato plato) {
        return mapper.toModel(repository.save(mapper.toEntity(plato)));
    }

    @Override
    public Optional<Plato> buscarPorId(Long id) {
        return repository.findById(id).map(mapper::toModel);
    }
}
