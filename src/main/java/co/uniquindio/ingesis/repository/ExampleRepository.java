package co.uniquindio.ingesis.repository;

import co.uniquindio.ingesis.model.Example;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExampleRepository implements PanacheRepository<Example> {
}