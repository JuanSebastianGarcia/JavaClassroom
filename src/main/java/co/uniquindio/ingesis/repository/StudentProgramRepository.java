package co.uniquindio.ingesis.repository;


import co.uniquindio.ingesis.model.StudentProgram;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class StudentProgramRepository implements PanacheRepository<StudentProgram> {
    public Optional<StudentProgram> findByStudentAndProgram(Integer studentId, Integer programId) {
        return find("student.id = ?1 and program.id = ?2", studentId, programId).firstResultOptional();
    }
}