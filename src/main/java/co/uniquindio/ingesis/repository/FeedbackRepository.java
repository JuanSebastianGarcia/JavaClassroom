package co.uniquindio.ingesis.repository;
import co.uniquindio.ingesis.model.Feedback;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class FeedbackRepository implements PanacheRepository<Feedback> {

    public List<Feedback> findByProgramId(Integer programId) {
        return list("program.id", programId);
    }

    public List<Feedback> findByTeacherId(Integer teacherId) {
        return list("teacher.id", teacherId);
    }

}