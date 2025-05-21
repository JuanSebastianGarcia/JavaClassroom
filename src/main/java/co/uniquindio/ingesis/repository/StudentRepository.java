package co.uniquindio.ingesis.repository;

import co.uniquindio.ingesis.model.Student;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

/**
 * Repository for managing student data using Panache.
 */
@ApplicationScoped
public class StudentRepository implements PanacheRepository<Student> {

    /**
     * Searches for a student by their cedula (document number).
     *
     * @param cedula The cedula (document number) of the student.
     * @return An Optional containing the student if found, otherwise empty.
     */
    public Optional<Student> findByCedula(String cedula) {
        return find("cedula", cedula).firstResultOptional();
    }

    /**
     * Searches for a student by their ID.
     *
     * @param id The ID of the student.
     * @return An Optional containing the student if found, otherwise empty.
     */
    public Optional<Student> findById(int id) {
        return find("id", id).firstResultOptional();
    }

    /**
     * Searches for a student by their email address.
     *
     * @param email The email of the student.
     * @return An Optional containing the student if found, otherwise empty.
     */
    public Optional<Student> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
}
