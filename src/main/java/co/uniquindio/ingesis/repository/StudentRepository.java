package co.uniquindio.ingesis.repository;

import co.uniquindio.ingesis.model.Student;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class StudentRepository implements PanacheRepository<Student> {

    public Optional<Student> findByCedula(String cedula) {
        return find("cedula", cedula).firstResultOptional();
    }
}
