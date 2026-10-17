package com.pragma.plazoleta.infrastructure.configuration;

import com.pragma.plazoleta.infrastructure.output.jpa.entity.CategoriaEntity;
import com.pragma.plazoleta.infrastructure.output.jpa.repository.ICategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/** Carga las categorías base del menú si la tabla está vacía (aún no hay HU para gestionarlas). */
@Component
public class CategoriaSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CategoriaSeeder.class);
    private static final List<String> CATEGORIAS_BASE =
            List.of("Entradas", "Sopas", "Platos fuertes", "Postres", "Bebidas");

    private final ICategoriaRepository repository;

    public CategoriaSeeder(ICategoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.saveAll(CATEGORIAS_BASE.stream()
                .map(nombre -> CategoriaEntity.builder().nombre(nombre).build())
                .toList());
        log.info("Categorías base creadas: {}", CATEGORIAS_BASE);
    }
}
