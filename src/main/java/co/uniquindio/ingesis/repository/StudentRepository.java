package co.uniquindio.ingesis.repository;

import co.uniquindio.ingesis.model.Student;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class StudentRepository implements PanacheRepository<Student> {

    /*
     * this method search a student by dorcument
     */
    public Optional<Student> findByCedula(String document) {
        return find("document", document).firstResultOptional();
    }


    /*
     * thi method remove a student by document
     * 
     * return true if the student has delete
     */
    public boolean removeByDocument(String document){
        long deletedCount = delete("document", document); 
        return deletedCount > 0; 
    }


}
