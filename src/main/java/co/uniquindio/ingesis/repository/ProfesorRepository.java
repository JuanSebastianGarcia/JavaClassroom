package co.uniquindio.ingesis.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import co.uniquindio.ingesis.model.entity.Profesor;
import java.util.Optional;

@ApplicationScoped
public class ProfesorRepository implements PanacheRepository<Profesor> {

    public Optional<Profesor> findByDocumento(String documento) {
        return find("documento", documento).firstResultOptional();
    }

    public Optional<Profesor> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
}