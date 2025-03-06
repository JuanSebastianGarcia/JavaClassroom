package co.uniquindio.ingesis.service.implement;

import java.util.Optional;

import jakarta.transaction.Transactional;
import co.uniquindio.ingesis.dto.teacherResource.TeacherDto;
import co.uniquindio.ingesis.exception.TeacherExistException;
import co.uniquindio.ingesis.exception.TeacherNotExistException;
import co.uniquindio.ingesis.model.Teacher;
import co.uniquindio.ingesis.repository.TeacherRepository;
import co.uniquindio.ingesis.service.interf.TeacherServiceInterface;
import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;

/*
 * This service is responsible for managing teachers
 */
@ApplicationScoped
public class TeacherService implements TeacherServiceInterface {


    
    /*
     * Constructor with dependency injection
     */
    public TeacherService(TeacherRepository teacherRepository) {

        this.teacherRepository = teacherRepository;
    }


    /*
     * Teacher's repository
     */
    private TeacherRepository teacherRepository;

    /*
     * This method adds a new teacher and validates it
     */
    @Override
    @Transactional
    public String addTeacher(TeacherDto teacherDto) throws TeacherExistException {

        Teacher new_teacher = buildTeacherFromDto(teacherDto);

        // Search teacher
        Optional<Teacher> teacher_exist = teacherRepository.findByCedula(new_teacher.getCedula());

        // Validate if the teacher already exists
        if (teacher_exist.isPresent()) {
            throw new TeacherExistException();
        }

        // Add teacher
        teacherRepository.persist(new_teacher);

        return "The teacher has been created";
    }

    /*
     * This method searches a teacher by document
     */
    @Override
    @Transactional
    public TeacherDto getTeacher(TeacherDto teacherDto) {

        // Search teacher
        Optional<Teacher> teacher = teacherRepository.findByCedula(teacherDto.cedula());

        // Validate if teacher does not exist
        if (teacher.isEmpty()) {
            throw new TeacherNotExistException();
        }

        // Build teacher dto
        return buildDtoFromTeacher(teacher.get());
    }

    /*
     * This method deletes a teacher
     */
    @Override
    @Transactional
    public String deleteTeacher(TeacherDto teacherDto) {

        // Search teacher
        Optional<Teacher> teacher = teacherRepository.findByCedula(teacherDto.cedula());

        if (teacher.isEmpty()) {
            throw new TeacherNotExistException();
        }

        // Delete teacher
        teacherRepository.delete(teacher.get());

        return "The teacher has been deleted";
    }

    /*
     * This method updates a teacher's information
     */
    @Override
    @Transactional
    public String updateTeacher(TeacherDto teacherDto) {

        // Search teacher
        Optional<Teacher> teacher_optional = teacherRepository.findByCedula(teacherDto.cedula());

        if (teacher_optional.isEmpty()) {
            throw new TeacherNotExistException();
        }

        Teacher teacher = teacher_optional.get();

        teacher.setName(teacherDto.name());
        teacher.setEmail(teacherDto.email());
        teacher.setPassword(hashPassword(teacherDto.password()));

        return "The teacher has been updated";
    }

    /*
     * This method builds a Teacher from a TeacherDto
     */
    private Teacher buildTeacherFromDto(TeacherDto teacherDto) {

        String password_hash = hashPassword(teacherDto.password());

        // Generate a teacher
        return new Teacher(teacherDto.id(), teacherDto.cedula(), teacherDto.name(), teacherDto.email(), password_hash);
    }

    /*
     * This method applies a hash to the password
     */
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    /*
     * This method builds a TeacherDto from a Teacher
     */
    private TeacherDto buildDtoFromTeacher(Teacher teacher) {
        return new TeacherDto(teacher.getId(), teacher.getCedula(), teacher.getName(), teacher.getEmail(), "");
    }
}