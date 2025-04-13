package co.uniquindio.ingesis.repository;


import co.uniquindio.ingesis.model.Program;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;


@ApplicationScoped
public class ProgramRepository implements PanacheRepository<Program> {

    @PersistenceContext
    EntityManager entityManager;

    public EntityManager getEntityManager() { 
        return entityManager;
    }

    public Optional<Program> findByCode(String code) {
        return find("code", code).firstResultOptional();
    }
}