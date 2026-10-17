package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.PageResult;
import com.pragma.plazoleta.domain.model.Restaurante;

public interface IRestauranteServicePort {

    /** HU2: crea un restaurante asociado a un usuario con rol PROPIETARIO. */
    Restaurante crearRestaurante(Restaurante restaurante);

    /** HU9: lista los restaurantes en orden alfabético, paginados. */
    PageResult<Restaurante> listarRestaurantes(int pagina, int tamano);
}
