package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.ForbiddenOperationException;
import com.pragma.plazoleta.domain.exception.InvalidDataException;
import com.pragma.plazoleta.domain.exception.ResourceNotFoundException;
import com.pragma.plazoleta.domain.model.Plato;
import com.pragma.plazoleta.domain.model.Restaurante;
import com.pragma.plazoleta.domain.spi.ICategoriaPersistencePort;
import com.pragma.plazoleta.domain.spi.IPlatoPersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantePersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatoUseCaseTest {

    private static final Long ID_PROPIETARIO = 10L;
    private static final Long ID_OTRO_PROPIETARIO = 99L;
    private static final Long ID_RESTAURANTE = 1L;
    private static final Long ID_PLATO = 7L;

    @Mock
    private IPlatoPersistencePort platoPersistencePort;
    @Mock
    private IRestaurantePersistencePort restaurantePersistencePort;
    @Mock
    private ICategoriaPersistencePort categoriaPersistencePort;

    private PlatoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new PlatoUseCase(platoPersistencePort, restaurantePersistencePort, categoriaPersistencePort);
    }

    private Restaurante restaurante() {
        return Restaurante.builder().id(ID_RESTAURANTE).nombre("El Corral").idPropietario(ID_PROPIETARIO).build();
    }

    private Plato platoNuevo() {
        return Plato.builder()
                .nombre("Bandeja paisa")
                .precio(32000)
                .descripcion("Frijoles, arroz y chicharrón")
                .urlImagen("https://cdn.ejemplo.com/bandeja.jpg")
                .idCategoria(2L)
                .idRestaurante(ID_RESTAURANTE)
                .build();
    }

    private Plato platoExistente() {
        return Plato.builder()
                .id(ID_PLATO)
                .nombre("Bandeja paisa")
                .precio(32000)
                .descripcion("Descripción original")
                .urlImagen("https://cdn.ejemplo.com/bandeja.jpg")
                .activo(true)
                .idCategoria(2L)
                .idRestaurante(ID_RESTAURANTE)
                .build();
    }

    // ---------- HU3: crear plato ----------

    @Test
    void crearPlato_conDatosValidosYPropietarioDelRestaurante_quedaActivo() {
        when(restaurantePersistencePort.buscarPorId(ID_RESTAURANTE)).thenReturn(Optional.of(restaurante()));
        when(categoriaPersistencePort.existePorId(2L)).thenReturn(true);
        when(platoPersistencePort.guardar(any(Plato.class))).thenAnswer(inv -> inv.getArgument(0));

        Plato resultado = useCase.crearPlato(platoNuevo(), ID_PROPIETARIO);

        assertThat(resultado.getActivo()).isTrue();
        assertThat(resultado.getIdRestaurante()).isEqualTo(ID_RESTAURANTE);
    }

    @Test
    void crearPlato_ignoraUnActivoFalseEnviadoPorElLlamador() {
        when(restaurantePersistencePort.buscarPorId(ID_RESTAURANTE)).thenReturn(Optional.of(restaurante()));
        when(categoriaPersistencePort.existePorId(2L)).thenReturn(true);
        when(platoPersistencePort.guardar(any(Plato.class))).thenAnswer(inv -> inv.getArgument(0));
        Plato plato = platoNuevo();
        plato.setActivo(false);

        assertThat(useCase.crearPlato(plato, ID_PROPIETARIO).getActivo()).isTrue();
    }

    @Test
    void crearPlato_deUnRestauranteAjeno_lanzaForbidden() {
        when(restaurantePersistencePort.buscarPorId(ID_RESTAURANTE)).thenReturn(Optional.of(restaurante()));

        assertThatThrownBy(() -> useCase.crearPlato(platoNuevo(), ID_OTRO_PROPIETARIO))
                .isInstanceOf(ForbiddenOperationException.class);
        verify(platoPersistencePort, never()).guardar(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -32000})
    void crearPlato_conPrecioNoPositivo_lanzaInvalidData(int precio) {
        Plato plato = platoNuevo();
        plato.setPrecio(precio);

        assertThatThrownBy(() -> useCase.crearPlato(plato, ID_PROPIETARIO))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("precio");
    }

    @Test
    void crearPlato_sinPrecio_lanzaInvalidData() {
        Plato plato = platoNuevo();
        plato.setPrecio(null);

        assertThatThrownBy(() -> useCase.crearPlato(plato, ID_PROPIETARIO))
                .isInstanceOf(InvalidDataException.class);
    }

    @Test
    void crearPlato_conCamposObligatoriosVacios_lanzaInvalidData() {
        Plato sinNombre = platoNuevo();
        sinNombre.setNombre(" ");
        Plato sinImagen = platoNuevo();
        sinImagen.setUrlImagen(null);
        Plato sinCategoria = platoNuevo();
        sinCategoria.setIdCategoria(null);

        assertThatThrownBy(() -> useCase.crearPlato(sinNombre, ID_PROPIETARIO))
                .isInstanceOf(InvalidDataException.class).hasMessageContaining("nombre");
        assertThatThrownBy(() -> useCase.crearPlato(sinImagen, ID_PROPIETARIO))
                .isInstanceOf(InvalidDataException.class).hasMessageContaining("imagen");
        assertThatThrownBy(() -> useCase.crearPlato(sinCategoria, ID_PROPIETARIO))
                .isInstanceOf(InvalidDataException.class).hasMessageContaining("categoría");
    }

    @Test
    void crearPlato_conRestauranteInexistente_lanzaNotFound() {
        when(restaurantePersistencePort.buscarPorId(ID_RESTAURANTE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.crearPlato(platoNuevo(), ID_PROPIETARIO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("restaurante");
    }

    @Test
    void crearPlato_conCategoriaInexistente_lanzaNotFound() {
        when(restaurantePersistencePort.buscarPorId(ID_RESTAURANTE)).thenReturn(Optional.of(restaurante()));
        when(categoriaPersistencePort.existePorId(2L)).thenReturn(false);

        assertThatThrownBy(() -> useCase.crearPlato(platoNuevo(), ID_PROPIETARIO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("categoría");
        verify(platoPersistencePort, never()).guardar(any());
    }

    // ---------- HU4: modificar plato ----------

    @Test
    void modificarPlato_conPrecioYDescripcion_actualizaSoloEsosCampos() {
        when(platoPersistencePort.buscarPorId(ID_PLATO)).thenReturn(Optional.of(platoExistente()));
        when(restaurantePersistencePort.buscarPorId(ID_RESTAURANTE)).thenReturn(Optional.of(restaurante()));
        when(platoPersistencePort.guardar(any(Plato.class))).thenAnswer(inv -> inv.getArgument(0));

        Plato resultado = useCase.modificarPlato(ID_PLATO, 35000, "Nueva descripción", ID_PROPIETARIO);

        assertThat(resultado.getPrecio()).isEqualTo(35000);
        assertThat(resultado.getDescripcion()).isEqualTo("Nueva descripción");
        assertThat(resultado.getNombre()).isEqualTo("Bandeja paisa");
        assertThat(resultado.getActivo()).isTrue();
    }

    @Test
    void modificarPlato_soloPrecio_noTocaLaDescripcion() {
        when(platoPersistencePort.buscarPorId(ID_PLATO)).thenReturn(Optional.of(platoExistente()));
        when(restaurantePersistencePort.buscarPorId(ID_RESTAURANTE)).thenReturn(Optional.of(restaurante()));
        when(platoPersistencePort.guardar(any(Plato.class))).thenAnswer(inv -> inv.getArgument(0));

        Plato resultado = useCase.modificarPlato(ID_PLATO, 40000, null, ID_PROPIETARIO);

        assertThat(resultado.getPrecio()).isEqualTo(40000);
        assertThat(resultado.getDescripcion()).isEqualTo("Descripción original");
    }

    @Test
    void modificarPlato_sinPrecioNiDescripcion_lanzaInvalidData() {
        assertThatThrownBy(() -> useCase.modificarPlato(ID_PLATO, null, null, ID_PROPIETARIO))
                .isInstanceOf(InvalidDataException.class);
    }

    @Test
    void modificarPlato_conPrecioInvalidoODescripcionVacia_lanzaInvalidData() {
        assertThatThrownBy(() -> useCase.modificarPlato(ID_PLATO, 0, null, ID_PROPIETARIO))
                .isInstanceOf(InvalidDataException.class).hasMessageContaining("precio");
        assertThatThrownBy(() -> useCase.modificarPlato(ID_PLATO, null, "   ", ID_PROPIETARIO))
                .isInstanceOf(InvalidDataException.class).hasMessageContaining("descripción");
    }

    @Test
    void modificarPlato_deOtroRestaurante_lanzaForbidden() {
        when(platoPersistencePort.buscarPorId(ID_PLATO)).thenReturn(Optional.of(platoExistente()));
        when(restaurantePersistencePort.buscarPorId(ID_RESTAURANTE)).thenReturn(Optional.of(restaurante()));

        assertThatThrownBy(() -> useCase.modificarPlato(ID_PLATO, 35000, null, ID_OTRO_PROPIETARIO))
                .isInstanceOf(ForbiddenOperationException.class);
        verify(platoPersistencePort, never()).guardar(any());
    }

    @Test
    void modificarPlato_inexistente_lanzaNotFound() {
        when(platoPersistencePort.buscarPorId(ID_PLATO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.modificarPlato(ID_PLATO, 35000, null, ID_PROPIETARIO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ---------- HU7: habilitar / deshabilitar ----------

    @Test
    void actualizarEstadoPlato_deshabilita_yDejaActivoEnFalse() {
        when(platoPersistencePort.buscarPorId(ID_PLATO)).thenReturn(Optional.of(platoExistente()));
        when(restaurantePersistencePort.buscarPorId(ID_RESTAURANTE)).thenReturn(Optional.of(restaurante()));
        when(platoPersistencePort.guardar(any(Plato.class))).thenAnswer(inv -> inv.getArgument(0));

        Plato resultado = useCase.actualizarEstadoPlato(ID_PLATO, false, ID_PROPIETARIO);

        assertThat(resultado.getActivo()).isFalse();
    }

    @Test
    void actualizarEstadoPlato_habilitaUnPlatoInactivo() {
        Plato inactivo = platoExistente();
        inactivo.setActivo(false);
        when(platoPersistencePort.buscarPorId(ID_PLATO)).thenReturn(Optional.of(inactivo));
        when(restaurantePersistencePort.buscarPorId(ID_RESTAURANTE)).thenReturn(Optional.of(restaurante()));
        when(platoPersistencePort.guardar(any(Plato.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(useCase.actualizarEstadoPlato(ID_PLATO, true, ID_PROPIETARIO).getActivo()).isTrue();
    }

    @Test
    void actualizarEstadoPlato_deRestauranteAjeno_lanzaForbidden() {
        when(platoPersistencePort.buscarPorId(ID_PLATO)).thenReturn(Optional.of(platoExistente()));
        when(restaurantePersistencePort.buscarPorId(ID_RESTAURANTE)).thenReturn(Optional.of(restaurante()));

        assertThatThrownBy(() -> useCase.actualizarEstadoPlato(ID_PLATO, false, ID_OTRO_PROPIETARIO))
                .isInstanceOf(ForbiddenOperationException.class);
        verify(platoPersistencePort, never()).guardar(any());
    }

    @Test
    void actualizarEstadoPlato_inexistente_lanzaNotFound() {
        when(platoPersistencePort.buscarPorId(ID_PLATO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.actualizarEstadoPlato(ID_PLATO, false, ID_PROPIETARIO))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
