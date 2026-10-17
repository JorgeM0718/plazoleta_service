package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Rol;

/** Puerto de salida hacia usuarios-service. */
public interface IUsuarioClientPort {

    /**
     * @throws com.pragma.plazoleta.domain.exception.ResourceNotFoundException si el usuario no existe
     * @throws com.pragma.plazoleta.domain.exception.ExternalServiceException si usuarios-service no responde
     */
    Rol obtenerRol(Long idUsuario);
}
