package co.uniquindio.ingesis.repository;

import co.uniquindio.ingesis.model.Program;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProgramRepository implements PanacheRepository<Program> {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * Returns the EntityManager instance.
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Updated method to find a program by its code and the student's ID.
     * 
     * @param code      program code
     * @param studentId ID of the student
     * @return Optional containing the found program or empty if not found
     */
    public Optional<Program> findByCodeAndStudentId(String code, Integer studentId) {
        return find("code = ?1 and studentId = ?2", code, studentId).firstResultOptional();
    }

    /**
     * Finds all programs belonging to a specific student.
     * 
     * @param studentId ID of the student
     * @return list of programs of the student
     */
    public List<Program> findByStudentId(Integer studentId) {
        return list("studentId", studentId);
    }

    /**
     * Optional: Finds a program by its code only.
     * 
     * @param code program code
     * @return Optional containing the found program or empty if not found
     */
    public Optional<Program> findByCode(String code) {
        return find("code", code).firstResultOptional();
    }

    /**
     * Checks if a program exists for a specific student by program code.
     * 
     * @param code      program code
     * @param studentId ID of the student
     * @return true if exists, false otherwise
     */
    public boolean existsByCodeAndStudentId(String code, Integer studentId) {
        return count("code = ?1 and studentId = ?2", code, studentId) > 0;
    }
}
