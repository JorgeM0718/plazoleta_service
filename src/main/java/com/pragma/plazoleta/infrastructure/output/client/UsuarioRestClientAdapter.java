package com.pragma.plazoleta.infrastructure.output.client;

import com.pragma.plazoleta.domain.exception.ExternalServiceException;
import com.pragma.plazoleta.domain.exception.ResourceNotFoundException;
import com.pragma.plazoleta.domain.model.Rol;
import com.pragma.plazoleta.domain.spi.IUsuarioClientPort;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

public class UsuarioRestClientAdapter implements IUsuarioClientPort {

    private final RestClient restClient;

    public UsuarioRestClientAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Rol obtenerRol(Long idUsuario) {
        UsuarioRolDto respuesta;
        try {
            respuesta = restClient.get()
                    .uri("/api/v1/usuarios/{id}/rol", idUsuario)
                    .retrieve()
                    .body(UsuarioRolDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("El usuario con id " + idUsuario + " no existe");
        } catch (RestClientException e) {
            throw new ExternalServiceException("No fue posible consultar el servicio de usuarios", e);
        }

        if (respuesta == null || respuesta.rol() == null) {
            throw new ExternalServiceException("El servicio de usuarios devolvió una respuesta inválida");
        }
        try {
            return Rol.valueOf(respuesta.rol());
        } catch (IllegalArgumentException e) {
            throw new ExternalServiceException("El servicio de usuarios devolvió un rol desconocido: " + respuesta.rol(), e);
        }
    }

    /** Contrato de GET /api/v1/usuarios/{id}/rol en usuarios-service. */
    private record UsuarioRolDto(Long id, String rol) {
    }
}
