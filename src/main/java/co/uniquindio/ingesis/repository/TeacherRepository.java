package co.uniquindio.ingesis.repository;
import co.uniquindio.ingesis.model.Teacher;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class TeacherRepository implements PanacheRepository<Teacher> {

    public Optional<Teacher> findByCedula(String cedula) {
        return find("cedula", cedula).firstResultOptional();
    }

    public Optional<Teacher> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
}
