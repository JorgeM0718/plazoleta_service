package com.pragma.plazoleta.infrastructure.configuration;

import com.pragma.plazoleta.domain.api.IPlatoServicePort;
import com.pragma.plazoleta.domain.api.IRestauranteServicePort;
import com.pragma.plazoleta.domain.spi.ICategoriaPersistencePort;
import com.pragma.plazoleta.domain.spi.IPlatoPersistencePort;
import com.pragma.plazoleta.domain.spi.IRestaurantePersistencePort;
import com.pragma.plazoleta.domain.spi.IUsuarioClientPort;
import com.pragma.plazoleta.domain.usecase.PlatoUseCase;
import com.pragma.plazoleta.domain.usecase.RestauranteUseCase;
import com.pragma.plazoleta.infrastructure.output.client.UsuarioRestClientAdapter;
import com.pragma.plazoleta.infrastructure.output.jpa.adapter.CategoriaJpaAdapter;
import com.pragma.plazoleta.infrastructure.output.jpa.adapter.PlatoJpaAdapter;
import com.pragma.plazoleta.infrastructure.output.jpa.adapter.RestauranteJpaAdapter;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.IPlatoEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.mapper.IRestauranteEntityMapper;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.ICategoriaRepository;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IPlatoRepository;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.IRestauranteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestClient;

/** Aquí se "cablea" el hexágono: puertos de salida -> adaptadores, casos de uso -> puertos de entrada. */
@Configuration
public class BeanConfiguration {

    @Bean
    public RestClient usuariosRestClient(@Value("${services.usuarios.base-url}") String baseUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(5000);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                // Reenvía el JWT de la petición en curso a usuarios-service
                .requestInterceptor((request, body, execution) -> {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication != null && authentication.getCredentials() instanceof String token) {
                        request.getHeaders().setBearerAuth(token);
                    }
                    return execution.execute(request, body);
                })
                .build();
    }

    @Bean
    public IUsuarioClientPort usuarioClientPort(RestClient usuariosRestClient) {
        return new UsuarioRestClientAdapter(usuariosRestClient);
    }

    @Bean
    public IRestaurantePersistencePort restaurantePersistencePort(IRestauranteRepository repository,
                                                                  IRestauranteEntityMapper mapper) {
        return new RestauranteJpaAdapter(repository, mapper);
    }

    @Bean
    public IPlatoPersistencePort platoPersistencePort(IPlatoRepository repository, IPlatoEntityMapper mapper) {
        return new PlatoJpaAdapter(repository, mapper);
    }

    @Bean
    public ICategoriaPersistencePort categoriaPersistencePort(ICategoriaRepository repository) {
        return new CategoriaJpaAdapter(repository);
    }

    @Bean
    public IRestauranteServicePort restauranteServicePort(IRestaurantePersistencePort restaurantePersistencePort,
                                                          IUsuarioClientPort usuarioClientPort) {
        return new RestauranteUseCase(restaurantePersistencePort, usuarioClientPort);
    }

    @Bean
    public IPlatoServicePort platoServicePort(IPlatoPersistencePort platoPersistencePort,
                                              IRestaurantePersistencePort restaurantePersistencePort,
                                              ICategoriaPersistencePort categoriaPersistencePort) {
        return new PlatoUseCase(platoPersistencePort, restaurantePersistencePort, categoriaPersistencePort);
    }
}
