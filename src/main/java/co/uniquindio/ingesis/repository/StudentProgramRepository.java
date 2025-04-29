package co.uniquindio.ingesis.repository;

import co.uniquindio.ingesis.model.StudentProgram;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class StudentProgramRepository implements PanacheRepository<StudentProgram> {

    public Optional<StudentProgram> findByStudentAndProgram(Integer studentId, Integer programId) {
        return find("student.id = ?1 and program.id = ?2", studentId, programId).firstResultOptional();
    }

    public List<Object[]> countResolvedProgramsByStudent() {
        return getEntityManager()
                .createQuery("""
                            SELECT sp.student.id, sp.student.name, COUNT(sp)
                            FROM StudentProgram sp
                            WHERE sp.resolved = true
                            GROUP BY sp.student.id, sp.student.name
                        """, Object[].class)
                .getResultList();
    }

}