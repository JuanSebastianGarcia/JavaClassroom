package co.uniquindio.ingesis.repository;

import co.uniquindio.ingesis.model.Teacher;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class TeacherRepository implements PanacheRepository<Teacher> {

    /**
     * Finds a teacher by their cedula (ID number).
     *
     * @param cedula The cedula of the teacher.
     * @return An Optional containing the teacher if found, otherwise empty.
     */
    public Optional<Teacher> findByCedula(String cedula) {
        return find("cedula", cedula).firstResultOptional();
    }

    /**
     * Finds a teacher by their email address.
     *
     * @param email The email of the teacher.
     * @return An Optional containing the teacher if found, otherwise empty.
     */
    public Optional<Teacher> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
}
