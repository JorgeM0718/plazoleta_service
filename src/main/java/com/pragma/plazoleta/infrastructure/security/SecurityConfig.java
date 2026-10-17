package com.pragma.plazoleta.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtTokenParser tokenParser,
                                                   RestSecurityErrorHandler errorHandler) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(errorHandler)
                        .accessDeniedHandler(errorHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        // HU2: solo administrador crea restaurantes
                        .requestMatchers(HttpMethod.POST, "/api/v1/restaurantes").hasRole("ADMINISTRADOR")
                        // HU9: el cliente lista restaurantes
                        .requestMatchers(HttpMethod.GET, "/api/v1/restaurantes").hasRole("CLIENTE")
                        // HU3, HU4, HU7: solo propietarios (la pertenencia al restaurante se valida en el dominio)
                        .requestMatchers(HttpMethod.POST, "/api/v1/platos").hasRole("PROPIETARIO")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/platos/**").hasRole("PROPIETARIO")
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(tokenParser), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
