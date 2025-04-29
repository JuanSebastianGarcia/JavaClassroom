package co.uniquindio.ingesis.repository;

import java.util.List;

//import org.jboss.logging.annotations.Param;
import co.uniquindio.ingesis.model.ExampleAssignment;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExampleAssignmentRepository implements PanacheRepository<ExampleAssignment> {

    public List<String> findCedulasByExampleId(Integer exampleId) {
        return getEntityManager()
                .createQuery("SELECT ea.cedulaEstudiante FROM ExampleAssignment ea WHERE ea.example.id = :exampleId",
                        String.class)
                .setParameter("exampleId", exampleId)
                .getResultList();
    }
}