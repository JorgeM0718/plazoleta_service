package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IRestauranteServicePort;
import com.pragma.plazoleta.domain.exception.DuplicateResourceException;
import com.pragma.plazoleta.domain.exception.InvalidDataException;
import com.pragma.plazoleta.domain.model.PageResult;
import com.pragma.plazoleta.domain.model.Restaurante;
import com.pragma.plazoleta.domain.model.Rol;
import com.pragma.plazoleta.domain.spi.IRestaurantePersistencePort;
import com.pragma.plazoleta.domain.spi.IUsuarioClientPort;

import java.util.regex.Pattern;

public class RestauranteUseCase implements IRestauranteServicePort {

    static final int TAMANO_PAGINA_MAXIMO = 100;
    private static final int TELEFONO_LONGITUD_MAXIMA = 13;
    private static final Pattern SOLO_NUMEROS = Pattern.compile("^\\d+$");
    private static final Pattern SOLO_NUMEROS_Y_ESPACIOS = Pattern.compile("^[\\d\\s]+$");
    private static final Pattern TELEFONO = Pattern.compile("^\\+?\\d+$");

    private final IRestaurantePersistencePort restaurantePersistencePort;
    private final IUsuarioClientPort usuarioClientPort;

    public RestauranteUseCase(IRestaurantePersistencePort restaurantePersistencePort,
                              IUsuarioClientPort usuarioClientPort) {
        this.restaurantePersistencePort = restaurantePersistencePort;
        this.usuarioClientPort = usuarioClientPort;
    }

    @Override
    public Restaurante crearRestaurante(Restaurante restaurante) {
        validarDatos(restaurante);

        String nit = restaurante.getNit().trim();
        if (restaurantePersistencePort.existePorNit(nit)) {
            throw new DuplicateResourceException("Ya existe un restaurante con el NIT " + nit);
        }

        Rol rolPropietario = usuarioClientPort.obtenerRol(restaurante.getIdPropietario());
        if (rolPropietario != Rol.PROPIETARIO) {
            throw new InvalidDataException(
                    "El usuario con id " + restaurante.getIdPropietario() + " no tiene el rol PROPIETARIO");
        }

        restaurante.setId(null);
        restaurante.setNombre(restaurante.getNombre().trim());
        restaurante.setNit(nit);
        restaurante.setDireccion(restaurante.getDireccion().trim());
        restaurante.setTelefono(restaurante.getTelefono().trim());
        restaurante.setUrlLogo(restaurante.getUrlLogo().trim());
        return restaurantePersistencePort.guardar(restaurante);
    }

    @Override
    public PageResult<Restaurante> listarRestaurantes(int pagina, int tamano) {
        if (pagina < 0) {
            throw new InvalidDataException("La página no puede ser negativa");
        }
        if (tamano < 1 || tamano > TAMANO_PAGINA_MAXIMO) {
            throw new InvalidDataException("El tamaño de página debe estar entre 1 y " + TAMANO_PAGINA_MAXIMO);
        }
        return restaurantePersistencePort.listarOrdenadosPorNombre(pagina, tamano);
    }

    private void validarDatos(Restaurante restaurante) {
        requerido(restaurante.getNombre(), "nombre");
        requerido(restaurante.getNit(), "NIT");
        requerido(restaurante.getDireccion(), "dirección");
        requerido(restaurante.getTelefono(), "teléfono");
        requerido(restaurante.getUrlLogo(), "URL del logo");
        if (restaurante.getIdPropietario() == null) {
            throw new InvalidDataException("El campo id del propietario es obligatorio");
        }

        if (SOLO_NUMEROS_Y_ESPACIOS.matcher(restaurante.getNombre().trim()).matches()) {
            throw new InvalidDataException("El nombre del restaurante no puede contener únicamente números");
        }
        if (!SOLO_NUMEROS.matcher(restaurante.getNit().trim()).matches()) {
            throw new InvalidDataException("El NIT debe ser únicamente numérico");
        }
        String telefono = restaurante.getTelefono().trim();
        if (telefono.length() > TELEFONO_LONGITUD_MAXIMA || !TELEFONO.matcher(telefono).matches()) {
            throw new InvalidDataException(
                    "El teléfono debe tener máximo 13 caracteres y solo puede contener números y el símbolo + al inicio");
        }
    }

    private void requerido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new InvalidDataException("El campo " + campo + " es obligatorio");
        }
    }
}
