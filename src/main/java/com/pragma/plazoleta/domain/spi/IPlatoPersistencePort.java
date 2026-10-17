package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Plato;

import java.util.Optional;

public interface IPlatoPersistencePort {

    Plato guardar(Plato plato);

    Optional<Plato> buscarPorId(Long id);
}
