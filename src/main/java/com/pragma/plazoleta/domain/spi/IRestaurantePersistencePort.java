package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.PageResult;
import com.pragma.plazoleta.domain.model.Restaurante;

import java.util.Optional;

public interface IRestaurantePersistencePort {

    Restaurante guardar(Restaurante restaurante);

    Optional<Restaurante> buscarPorId(Long id);

    boolean existePorNit(String nit);

    PageResult<Restaurante> listarOrdenadosPorNombre(int pagina, int tamano);
}
