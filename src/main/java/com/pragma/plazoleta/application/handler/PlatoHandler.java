package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.PlatoEstadoRequest;
import com.pragma.plazoleta.application.dto.request.PlatoRequest;
import com.pragma.plazoleta.application.dto.request.PlatoUpdateRequest;
import com.pragma.plazoleta.application.dto.response.PlatoResponse;
import com.pragma.plazoleta.application.mapper.IPlatoDtoMapper;
import com.pragma.plazoleta.domain.api.IPlatoServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlatoHandler {

    private final IPlatoServicePort platoServicePort;
    private final IPlatoDtoMapper mapper;

    public PlatoResponse crearPlato(PlatoRequest request, Long idPropietario) {
        return mapper.toResponse(platoServicePort.crearPlato(mapper.toModel(request), idPropietario));
    }

    public PlatoResponse modificarPlato(Long idPlato, PlatoUpdateRequest request, Long idPropietario) {
        return mapper.toResponse(
                platoServicePort.modificarPlato(idPlato, request.precio(), request.descripcion(), idPropietario));
    }

    public PlatoResponse actualizarEstado(Long idPlato, PlatoEstadoRequest request, Long idPropietario) {
        return mapper.toResponse(
                platoServicePort.actualizarEstadoPlato(idPlato, request.activo(), idPropietario));
    }
}
