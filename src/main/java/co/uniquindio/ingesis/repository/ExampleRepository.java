package co.uniquindio.ingesis.repository;

import co.uniquindio.ingesis.model.Example;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for managing Example entities using PanacheRepository.
 */
@ApplicationScoped
public class ExampleRepository implements PanacheRepository<Example> {
    // You can add custom database query methods here if needed
}
