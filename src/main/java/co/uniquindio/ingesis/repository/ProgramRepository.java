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

    public EntityManager getEntityManager() {
        return entityManager;
    }

    // Método actualizado para buscar por código y studentId
    public Optional<Program> findByCodeAndStudentId(String code, Integer studentId) {
        return find("code = ?1 and studentId = ?2", code, studentId).firstResultOptional();
    }

    // Método para buscar todos los programas de un estudiante
    public List<Program> findByStudentId(Integer studentId) {
        return list("studentId", studentId);
    }

    // Mantén el método original si aún lo necesitas (opcional)
    public Optional<Program> findByCode(String code) {
        return find("code", code).firstResultOptional();
    }

    // Método para verificar si un programa existe para un estudiante específico
    public boolean existsByCodeAndStudentId(String code, Integer studentId) {
        return count("code = ?1 and studentId = ?2", code, studentId) > 0;
    }
}