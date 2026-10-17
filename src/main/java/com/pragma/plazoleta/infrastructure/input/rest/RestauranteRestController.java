package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.request.RestauranteRequest;
import com.pragma.plazoleta.application.dto.response.PageResponse;
import com.pragma.plazoleta.application.dto.response.RestauranteResponse;
import com.pragma.plazoleta.application.dto.response.RestauranteResumenResponse;
import com.pragma.plazoleta.application.handler.RestauranteHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurantes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Restaurantes", description = "HU2 y HU9")
public class RestauranteRestController {

    private final RestauranteHandler restauranteHandler;

    @PostMapping
    @Operation(summary = "Crear restaurante (HU2)",
            description = "Solo administrador. El id del propietario debe corresponder a un usuario con rol PROPIETARIO.")
    @ApiResponse(responseCode = "201", description = "Restaurante creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o el usuario no es propietario")
    @ApiResponse(responseCode = "401", description = "Sin sesión")
    @ApiResponse(responseCode = "403", description = "No es administrador")
    @ApiResponse(responseCode = "404", description = "El propietario no existe")
    @ApiResponse(responseCode = "409", description = "NIT ya registrado")
    public ResponseEntity<RestauranteResponse> crearRestaurante(@Valid @RequestBody RestauranteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restauranteHandler.crearRestaurante(request));
    }

    @GetMapping
    @Operation(summary = "Listar restaurantes (HU9)",
            description = "Solo cliente. Orden alfabético por nombre, paginado. Devuelve nombre y logo.")
    @ApiResponse(responseCode = "200", description = "Página de restaurantes")
    @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    public ResponseEntity<PageResponse<RestauranteResumenResponse>> listarRestaurantes(
            @Parameter(description = "Número de página (empieza en 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Elementos por página (1 a 100)") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(restauranteHandler.listarRestaurantes(page, size));
    }
}
