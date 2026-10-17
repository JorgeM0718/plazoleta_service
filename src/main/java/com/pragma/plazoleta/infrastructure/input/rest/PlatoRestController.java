package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.request.PlatoEstadoRequest;
import com.pragma.plazoleta.application.dto.request.PlatoRequest;
import com.pragma.plazoleta.application.dto.request.PlatoUpdateRequest;
import com.pragma.plazoleta.application.dto.response.PlatoResponse;
import com.pragma.plazoleta.application.handler.PlatoHandler;
import com.pragma.plazoleta.infrastructure.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/platos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Platos", description = "HU3, HU4 y HU7 - Solo el propietario del restaurante")
public class PlatoRestController {

    private final PlatoHandler platoHandler;

    @PostMapping
    @Operation(summary = "Crear plato (HU3)",
            description = "El plato queda activo por defecto y asociado al restaurante indicado, que debe ser del propietario autenticado.")
    @ApiResponse(responseCode = "201", description = "Plato creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "403", description = "El restaurante no pertenece al propietario")
    @ApiResponse(responseCode = "404", description = "Restaurante o categoría inexistente")
    public ResponseEntity<PlatoResponse> crearPlato(@Valid @RequestBody PlatoRequest request,
                                                    @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(platoHandler.crearPlato(request, usuario.id()));
    }

    @PatchMapping("/{idPlato}")
    @Operation(summary = "Modificar plato (HU4)", description = "Solo se pueden modificar el precio y la descripción.")
    @ApiResponse(responseCode = "200", description = "Plato actualizado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "403", description = "El plato es de otro restaurante")
    @ApiResponse(responseCode = "404", description = "Plato inexistente")
    public ResponseEntity<PlatoResponse> modificarPlato(@PathVariable Long idPlato,
                                                        @Valid @RequestBody PlatoUpdateRequest request,
                                                        @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(platoHandler.modificarPlato(idPlato, request, usuario.id()));
    }

    @PatchMapping("/{idPlato}/estado")
    @Operation(summary = "Habilitar / deshabilitar plato (HU7)")
    @ApiResponse(responseCode = "200", description = "Estado actualizado")
    @ApiResponse(responseCode = "403", description = "El plato es de otro restaurante")
    @ApiResponse(responseCode = "404", description = "Plato inexistente")
    public ResponseEntity<PlatoResponse> actualizarEstado(@PathVariable Long idPlato,
                                                          @Valid @RequestBody PlatoEstadoRequest request,
                                                          @Parameter(hidden = true) @AuthenticationPrincipal AuthenticatedUser usuario) {
        return ResponseEntity.ok(platoHandler.actualizarEstado(idPlato, request, usuario.id()));
    }
}
