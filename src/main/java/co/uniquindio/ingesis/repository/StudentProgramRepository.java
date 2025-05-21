package co.uniquindio.ingesis.repository;

import co.uniquindio.ingesis.model.StudentProgram;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class StudentProgramRepository implements PanacheRepository<StudentProgram> {

    /**
     * Finds a StudentProgram entity by student ID and program ID.
     * 
     * @param studentId the ID of the student
     * @param programId the ID of the program
     * @return Optional containing the found StudentProgram or empty if not found
     */
    public Optional<StudentProgram> findByStudentAndProgram(Integer studentId, Integer programId) {
        return find("student.id = ?1 and program.id = ?2", studentId, programId).firstResultOptional();
    }

    /**
     * Counts the number of resolved programs grouped by student.
     * Returns a list of Object arrays where each element contains:
     * - Student ID
     * - Student name
     * - Count of resolved programs
     * - ID of the user who marked them resolved (resolvedBy)
     * 
     * @return List of Object arrays with the grouped results
     */
    public List<Object[]> countResolvedProgramsByStudent() {
        return getEntityManager()
                .createQuery("""
                            SELECT sp.student.id, sp.student.name, COUNT(sp), sp.resolvedBy.id
                            FROM StudentProgram sp
                            WHERE sp.resolved = true
                            GROUP BY sp.student.id, sp.student.name, sp.resolvedBy.id
                        """, Object[].class)
                .getResultList();
    }

}
