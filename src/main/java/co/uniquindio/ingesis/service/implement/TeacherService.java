package co.uniquindio.ingesis.service.implement;

import java.util.Optional;
import java.util.UUID;

import jakarta.transaction.Transactional;
import co.uniquindio.ingesis.dto.teacherResource.TeacherDto;
import co.uniquindio.ingesis.exception.TeacherExistException;
import co.uniquindio.ingesis.exception.TeacherNotExistException;
import co.uniquindio.ingesis.model.Teacher;
import co.uniquindio.ingesis.model.enumerations.StatusAcountEnum;
import co.uniquindio.ingesis.repository.TeacherRepository;
import co.uniquindio.ingesis.service.interfaces.TeacherServiceInterface;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.mindrot.jbcrypt.BCrypt;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/*
 * This service is responsible for managing teachers
 */
@ApplicationScoped
public class TeacherService implements TeacherServiceInterface {

    /*
     * Logger for this class
     */
    private static final Logger logger = LogManager.getLogger(TeacherService.class);

    /*
     * Teacher repository for database operations
     */
    private TeacherRepository teacherRepository;

    @Inject
    private VerificationService verificationService;

    /*
     * Constructor with dependency injection
     */
    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    /*
     * This method adds a new teacher and validates it
     */
    @Override
    @PermitAll
    public String addTeacher(TeacherDto teacherDto) throws TeacherExistException {
        // Ejecuta en una nueva transacción
        String token = createTeacherTransactional(teacherDto);

        // Envío fuera de la transacción principal
        try {
            verificationService.sendVerificationEmail(teacherDto.email(), token);
            logger.info("Correo de verificación enviado a {}", teacherDto.email());
        } catch (Exception e) {
            logger.error("Error al enviar correo de verificación: {}", e.getMessage(), e);
        }

        return "The teacher has been created";
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public String createTeacherTransactional(TeacherDto teacherDto) throws TeacherExistException {
        Teacher new_teacher = buildTeacherFromDto(teacherDto);
        logger.info("Attempting to create teacher with Cedula: {}", new_teacher.getCedula());

        Optional<Teacher> teacher_exist = teacherRepository.findByCedula(new_teacher.getCedula());
        if (teacher_exist.isPresent()) {
            throw new TeacherExistException();
        }

        String token = UUID.randomUUID().toString();
        new_teacher.setToken(token);

        teacherRepository.persist(new_teacher);
        logger.info("Teacher created successfully with Cedula: {}", new_teacher.getCedula());

        return token;
    }

    /*
     * This method searches a teacher by document
     */
    @Override
    @PermitAll
    @RolesAllowed({ "teacher" })
    public TeacherDto getTeacher(TeacherDto teacherDto) {
        logger.info("Fetching teacher with Cedula: {}", teacherDto.cedula());

        Optional<Teacher> teacher = teacherRepository.findByCedula(teacherDto.cedula());

        if (teacher.isEmpty()) {
            logger.warn("Teacher with Cedula {} not found", teacherDto.cedula());
            throw new TeacherNotExistException();
        }

        logger.info("Teacher with Cedula {} found successfully", teacherDto.cedula());
        return buildDtoFromTeacher(teacher.get());
    }

    /*
     * This method deletes a teacher
     */
    @Override
    @RolesAllowed({ "teacher" })
    @Transactional
    public String deleteTeacher(TeacherDto teacherDto) {
        logger.info("Attempting to delete teacher with Cedula: {}", teacherDto.cedula());

        Optional<Teacher> teacher = teacherRepository.findByCedula(teacherDto.cedula());

        if (teacher.isEmpty()) {
            logger.warn("Teacher with Cedula {} not found", teacherDto.cedula());
            throw new TeacherNotExistException();
        }

        teacherRepository.delete(teacher.get());
        logger.info("Teacher with Cedula {} deleted successfully", teacherDto.cedula());

        return "The teacher has been deleted";
    }

    /*
     * This method updates a teacher's information
     */
    @Override
    @RolesAllowed({ "teacher" })
    @Transactional
    public String updateTeacher(TeacherDto teacherDto) {
        logger.info("Attempting to update teacher with Cedula: {}", teacherDto.cedula());

        Optional<Teacher> teacher_optional = teacherRepository.findByCedula(teacherDto.cedula());

        if (teacher_optional.isEmpty()) {
            logger.warn("Teacher with Cedula {} not found", teacherDto.cedula());
            throw new TeacherNotExistException();
        }

        Teacher teacher = teacher_optional.get();
        teacher.setName(teacherDto.name());
        teacher.setEmail(teacherDto.email());
        teacher.setPassword(hashPassword(teacherDto.password()));
        teacherRepository.persist(teacher);

        logger.info("Teacher with Cedula {} updated successfully", teacherDto.cedula());
        return "The teacher has been updated";
    }

    /*
     * This method builds a Teacher from a TeacherDto
     */
    private Teacher buildTeacherFromDto(TeacherDto teacherDto) {
        String password_hash = hashPassword(teacherDto.password());
        return new Teacher(teacherDto.id(), teacherDto.cedula(), teacherDto.name(), teacherDto.email(), password_hash,
                StatusAcountEnum.PENDING, "");
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
        return new TeacherDto(teacher.getId(), teacher.getCedula(), teacher.getName(), teacher.getEmail(), "",
                teacher.getStatus());
    }
}
