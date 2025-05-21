package co.uniquindio.ingesis.repository;

import co.uniquindio.ingesis.model.Feedback;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Repository for managing Feedback entities.
 */
@ApplicationScoped
public class FeedbackRepository implements PanacheRepository<Feedback> {

    /**
     * Find all feedbacks related to a specific program by its ID.
     * 
     * @param programId the ID of the program
     * @return list of Feedback objects for the program
     */
    public List<Feedback> findByProgramId(Integer programId) {
        return list("program.id", programId);
    }

    /**
     * Find all feedbacks given by a specific teacher by their ID.
     * 
     * @param teacherId the ID of the teacher
     * @return list of Feedback objects provided by the teacher
     */
    public List<Feedback> findByTeacherId(Integer teacherId) {
        return list("teacher.id", teacherId);
    }
}
