package com.pragma.plazoleta.infrastructure.output.jpa.adapter;

import com.pragma.plazoleta.domain.spi.ICategoriaPersistencePort;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.ICategoriaRepository;

public class CategoriaJpaAdapter implements ICategoriaPersistencePort {

    private final ICategoriaRepository repository;

    public CategoriaJpaAdapter(ICategoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }
}
