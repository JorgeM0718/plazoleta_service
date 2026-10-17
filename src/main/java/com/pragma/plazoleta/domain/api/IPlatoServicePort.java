package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.Plato;

public interface IPlatoServicePort {

    /** HU3: crea un plato en un restaurante del propietario solicitante. */
    Plato crearPlato(Plato plato, Long idPropietarioSolicitante);

    /** HU4: modifica únicamente precio y/o descripción (los que vengan en null no se tocan). */
    Plato modificarPlato(Long idPlato, Integer precio, String descripcion, Long idPropietarioSolicitante);

    /** HU7: habilita o deshabilita un plato. */
    Plato actualizarEstadoPlato(Long idPlato, boolean activo, Long idPropietarioSolicitante);
}
