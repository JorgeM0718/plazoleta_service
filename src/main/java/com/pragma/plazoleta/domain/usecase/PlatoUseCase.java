package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IPlatoServicePort;
import com.pragma.plazoleta.domain.exception.ForbiddenOperationException;
import com.pragma.plazoleta.domain.exception.InvalidDataException;
import com.pragma.plazoleta.domain.exception.ResourceNotFoundException;
import com.pragma.plazoleta.domain.model.Plato;
import com.pragma.plazoleta.domain.model.Restaurante;
import com.pragma.plazoleta.domain.spi.ICategoriaPersistencePort;
import com.pragma.plazoleta.domain.spi.IPlatoPersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantePersistencePort;

public class PlatoUseCase implements IPlatoServicePort {

    private final IPlatoPersistencePort platoPersistencePort;
    private final IRestaurantePersistencePort restaurantePersistencePort;
    private final ICategoriaPersistencePort categoriaPersistencePort;

    public PlatoUseCase(IPlatoPersistencePort platoPersistencePort,
                        IRestaurantePersistencePort restaurantePersistencePort,
                        ICategoriaPersistencePort categoriaPersistencePort) {
        this.platoPersistencePort = platoPersistencePort;
        this.restaurantePersistencePort = restaurantePersistencePort;
        this.categoriaPersistencePort = categoriaPersistencePort;
    }

    @Override
    public Plato crearPlato(Plato plato, Long idPropietarioSolicitante) {
        requerido(plato.getNombre(), "nombre del plato");
        requerido(plato.getDescripcion(), "descripción");
        requerido(plato.getUrlImagen(), "URL de la imagen");
        if (plato.getIdCategoria() == null) {
            throw new InvalidDataException("El campo categoría es obligatorio");
        }
        if (plato.getIdRestaurante() == null) {
            throw new InvalidDataException("El campo restaurante es obligatorio");
        }
        validarPrecio(plato.getPrecio());

        Restaurante restaurante = restaurantePersistencePort.buscarPorId(plato.getIdRestaurante())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El restaurante con id " + plato.getIdRestaurante() + " no existe"));
        validarPropietario(restaurante, idPropietarioSolicitante,
                "Solo el propietario del restaurante puede crear platos en él");

        if (!categoriaPersistencePort.existePorId(plato.getIdCategoria())) {
            throw new ResourceNotFoundException("La categoría con id " + plato.getIdCategoria() + " no existe");
        }

        plato.setId(null);
        plato.setNombre(plato.getNombre().trim());
        plato.setDescripcion(plato.getDescripcion().trim());
        plato.setUrlImagen(plato.getUrlImagen().trim());
        plato.setActivo(true);
        return platoPersistencePort.guardar(plato);
    }

    @Override
    public Plato modificarPlato(Long idPlato, Integer precio, String descripcion, Long idPropietarioSolicitante) {
        if (precio == null && descripcion == null) {
            throw new InvalidDataException("Debes enviar al menos el precio o la descripción");
        }
        if (precio != null) {
            validarPrecio(precio);
        }
        if (descripcion != null && descripcion.isBlank()) {
            throw new InvalidDataException("La descripción no puede estar vacía");
        }

        Plato plato = obtenerPlatoDelPropietario(idPlato, idPropietarioSolicitante);
        if (precio != null) {
            plato.setPrecio(precio);
        }
        if (descripcion != null) {
            plato.setDescripcion(descripcion.trim());
        }
        return platoPersistencePort.guardar(plato);
    }

    @Override
    public Plato actualizarEstadoPlato(Long idPlato, boolean activo, Long idPropietarioSolicitante) {
        Plato plato = obtenerPlatoDelPropietario(idPlato, idPropietarioSolicitante);
        plato.setActivo(activo);
        return platoPersistencePort.guardar(plato);
    }

    private Plato obtenerPlatoDelPropietario(Long idPlato, Long idPropietarioSolicitante) {
        Plato plato = platoPersistencePort.buscarPorId(idPlato)
                .orElseThrow(() -> new ResourceNotFoundException("El plato con id " + idPlato + " no existe"));
        Restaurante restaurante = restaurantePersistencePort.buscarPorId(plato.getIdRestaurante())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El restaurante con id " + plato.getIdRestaurante() + " no existe"));
        validarPropietario(restaurante, idPropietarioSolicitante,
                "No puedes modificar platos de un restaurante que no es tuyo");
        return plato;
    }

    private void validarPropietario(Restaurante restaurante, Long idSolicitante, String mensaje) {
        if (idSolicitante == null || !idSolicitante.equals(restaurante.getIdPropietario())) {
            throw new ForbiddenOperationException(mensaje);
        }
    }

    private void validarPrecio(Integer precio) {
        if (precio == null || precio <= 0) {
            throw new InvalidDataException("El precio debe ser un número entero positivo mayor a 0");
        }
    }

    private void requerido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new InvalidDataException("El campo " + campo + " es obligatorio");
        }
    }
}
