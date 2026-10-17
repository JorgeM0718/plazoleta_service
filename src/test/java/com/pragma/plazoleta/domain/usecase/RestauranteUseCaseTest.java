package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.DuplicateResourceException;
import com.pragma.plazoleta.domain.exception.InvalidDataException;
import com.pragma.plazoleta.domain.exception.ResourceNotFoundException;
import com.pragma.plazoleta.domain.model.PageResult;
import com.pragma.plazoleta.domain.model.Restaurante;
import com.pragma.plazoleta.domain.model.Rol;
import com.pragma.plazoleta.domain.spi.IRestaurantePersistencePort;
import com.pragma.plazoleta.domain.spi.IUsuarioClientPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestauranteUseCaseTest {

    @Mock
    private IRestaurantePersistencePort persistencePort;
    @Mock
    private IUsuarioClientPort usuarioClientPort;

    private RestauranteUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RestauranteUseCase(persistencePort, usuarioClientPort);
    }

    private Restaurante restauranteValido() {
        return Restaurante.builder()
                .nombre("El Corral 24")
                .nit("900123456")
                .direccion("Calle 10 # 5-20")
                .telefono("+573005698325")
                .urlLogo("https://cdn.ejemplo.com/logo.png")
                .idPropietario(5L)
                .build();
    }

    // ---------- HU2: crear restaurante ----------

    @Test
    void crearRestaurante_conDatosValidosYPropietario_guardaElRestaurante() {
        when(persistencePort.existePorNit("900123456")).thenReturn(false);
        when(usuarioClientPort.obtenerRol(5L)).thenReturn(Rol.PROPIETARIO);
        when(persistencePort.guardar(any(Restaurante.class))).thenAnswer(inv -> inv.getArgument(0));

        Restaurante resultado = useCase.crearRestaurante(restauranteValido());

        assertThat(resultado.getNombre()).isEqualTo("El Corral 24");
        assertThat(resultado.getIdPropietario()).isEqualTo(5L);
        verify(persistencePort).guardar(any(Restaurante.class));
    }

    @Test
    void crearRestaurante_conNombreConNumerosYLetras_esValido() {
        when(persistencePort.existePorNit("900123456")).thenReturn(false);
        when(usuarioClientPort.obtenerRol(5L)).thenReturn(Rol.PROPIETARIO);
        when(persistencePort.guardar(any(Restaurante.class))).thenAnswer(inv -> inv.getArgument(0));
        Restaurante restaurante = restauranteValido();
        restaurante.setNombre("24 Horas");

        assertThat(useCase.crearRestaurante(restaurante).getNombre()).isEqualTo("24 Horas");
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "  987  ", "1 2 3"})
    void crearRestaurante_conNombreSoloNumeros_lanzaInvalidData(String nombre) {
        Restaurante restaurante = restauranteValido();
        restaurante.setNombre(nombre);

        assertThatThrownBy(() -> useCase.crearRestaurante(restaurante))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("únicamente números");
        verify(persistencePort, never()).guardar(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"90012345A", "900-123-456", "900 123", "+900123"})
    void crearRestaurante_conNitNoNumerico_lanzaInvalidData(String nit) {
        Restaurante restaurante = restauranteValido();
        restaurante.setNit(nit);

        assertThatThrownBy(() -> useCase.crearRestaurante(restaurante))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("NIT");
    }

    @ParameterizedTest
    @ValueSource(strings = {"+57300569832512", "abc123", "57+3005698325", "300 569 8325"})
    void crearRestaurante_conTelefonoInvalido_lanzaInvalidData(String telefono) {
        Restaurante restaurante = restauranteValido();
        restaurante.setTelefono(telefono);

        assertThatThrownBy(() -> useCase.crearRestaurante(restaurante))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("teléfono");
    }

    @Test
    void crearRestaurante_conCamposObligatoriosFaltantes_lanzaInvalidData() {
        Restaurante sinDireccion = restauranteValido();
        sinDireccion.setDireccion(" ");
        Restaurante sinPropietario = restauranteValido();
        sinPropietario.setIdPropietario(null);

        assertThatThrownBy(() -> useCase.crearRestaurante(sinDireccion))
                .isInstanceOf(InvalidDataException.class).hasMessageContaining("dirección");
        assertThatThrownBy(() -> useCase.crearRestaurante(sinPropietario))
                .isInstanceOf(InvalidDataException.class).hasMessageContaining("propietario");
    }

    @Test
    void crearRestaurante_conNitYaRegistrado_lanzaDuplicate() {
        when(persistencePort.existePorNit("900123456")).thenReturn(true);

        assertThatThrownBy(() -> useCase.crearRestaurante(restauranteValido()))
                .isInstanceOf(DuplicateResourceException.class);
        verify(persistencePort, never()).guardar(any());
    }

    @Test
    void crearRestaurante_conUsuarioQueNoEsPropietario_lanzaInvalidData() {
        when(persistencePort.existePorNit("900123456")).thenReturn(false);
        when(usuarioClientPort.obtenerRol(5L)).thenReturn(Rol.CLIENTE);

        assertThatThrownBy(() -> useCase.crearRestaurante(restauranteValido()))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("PROPIETARIO");
        verify(persistencePort, never()).guardar(any());
    }

    @Test
    void crearRestaurante_conPropietarioInexistente_propagaNotFound() {
        when(persistencePort.existePorNit("900123456")).thenReturn(false);
        when(usuarioClientPort.obtenerRol(5L)).thenThrow(new ResourceNotFoundException("El usuario con id 5 no existe"));

        assertThatThrownBy(() -> useCase.crearRestaurante(restauranteValido()))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(persistencePort, never()).guardar(any());
    }

    // ---------- HU9: listar restaurantes ----------

    @Test
    void listarRestaurantes_conParametrosValidos_delegaEnElPuerto() {
        PageResult<Restaurante> pagina = new PageResult<>(List.of(restauranteValido()), 0, 10, 1, 1);
        when(persistencePort.listarOrdenadosPorNombre(0, 10)).thenReturn(pagina);

        assertThat(useCase.listarRestaurantes(0, 10)).isEqualTo(pagina);
    }

    @Test
    void listarRestaurantes_conPaginaNegativa_lanzaInvalidData() {
        assertThatThrownBy(() -> useCase.listarRestaurantes(-1, 10))
                .isInstanceOf(InvalidDataException.class);
        verify(persistencePort, never()).listarOrdenadosPorNombre(0, 10);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -5, 101})
    void listarRestaurantes_conTamanoFueraDeRango_lanzaInvalidData(int tamano) {
        assertThatThrownBy(() -> useCase.listarRestaurantes(0, tamano))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("tamaño");
    }
}
