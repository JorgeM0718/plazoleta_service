package com.pragma.plazoleta.infrastructure.security;

import com.pragma.plazoleta.domain.model.Rol;

public record AuthenticatedUser(Long id, String correo, Rol rol) {
}
