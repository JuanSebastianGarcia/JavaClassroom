package co.uniquindio.ingesis.repository;

import java.util.List;

import co.uniquindio.ingesis.model.ExampleAssignment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExampleAssignmentRepository implements PanacheRepository<ExampleAssignment> {

    /**
     * Retrieves the list of student IDs assigned to the example with the given ID.
     *
     * @param exampleId the ID of the example
     * @return list of student IDs
     */
    public List<String> findCedulasByExampleId(Integer exampleId) {
        return getEntityManager()
                .createQuery("SELECT ea.cedulaEstudiante FROM ExampleAssignment ea WHERE ea.example.id = :exampleId",
                        String.class)
                .setParameter("exampleId", exampleId)
                .getResultList();
    }

    /**
     * Checks if there is any assignment for the example with the given ID.
     *
     * @param exampleId the ID of the example
     * @return true if at least one assignment exists, false otherwise
     */
    public boolean existsByExampleId(Integer exampleId) {
        Long count = getEntityManager()
                .createQuery("SELECT COUNT(ea) FROM ExampleAssignment ea WHERE ea.example.id = :exampleId", Long.class)
                .setParameter("exampleId", exampleId)
                .getSingleResult();
        return count > 0;
    }
}
