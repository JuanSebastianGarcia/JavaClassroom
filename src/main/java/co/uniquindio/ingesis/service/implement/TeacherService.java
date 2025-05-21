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

/**
 * Service class responsible for managing teacher-related operations including
 * creation, retrieval, updating, and deletion of teacher entities.
 */
@ApplicationScoped
public class TeacherService implements TeacherServiceInterface {

    private static final Logger logger = LogManager.getLogger(TeacherService.class);

    private TeacherRepository teacherRepository;

    @Inject
    private VerificationService verificationService;

    /**
     * Constructor for dependency injection of the teacher repository.
     * 
     * @param teacherRepository the repository handling teacher persistence
     *                          operations
     */
    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    /**
     * Adds a new teacher to the system, generating a verification token and
     * sending a verification email.
     * 
     * @param teacherDto the data transfer object containing teacher details
     * @return a confirmation message upon successful creation
     * @throws TeacherExistException if a teacher with the given document already
     *                               exists
     */
    @Override
    @PermitAll
    public String addTeacher(TeacherDto teacherDto) throws TeacherExistException {
        String token = createTeacherTransactional(teacherDto);

        try {
            verificationService.sendVerificationEmail(teacherDto.email(), token);
            logger.info("Verification email sent to {}", teacherDto.email());
        } catch (Exception e) {
            logger.error("Failed to send verification email: {}", e.getMessage(), e);
        }

        return "The teacher has been created";
    }

    /**
     * Creates a teacher within a new transactional context. This method performs
     * validation to prevent duplicate teachers.
     * 
     * @param teacherDto the data transfer object containing teacher details
     * @return the verification token generated for the new teacher
     * @throws TeacherExistException if a teacher with the given document already
     *                               exists
     */
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public String createTeacherTransactional(TeacherDto teacherDto) throws TeacherExistException {
        Teacher newTeacher = buildTeacherFromDto(teacherDto);
        logger.info("Attempting to create teacher with Cedula: {}", newTeacher.getCedula());

        Optional<Teacher> existingTeacher = teacherRepository.findByCedula(newTeacher.getCedula());
        if (existingTeacher.isPresent()) {
            throw new TeacherExistException();
        }

        String token = UUID.randomUUID().toString();
        newTeacher.setToken(token);

        teacherRepository.persist(newTeacher);
        logger.info("Teacher created successfully with Cedula: {}", newTeacher.getCedula());

        return token;
    }

    /**
     * Retrieves a teacher based on their document ID.
     * 
     * @param teacherDto the data transfer object containing the teacher's document
     *                   ID
     * @return a TeacherDto representing the found teacher
     * @throws TeacherNotExistException if no teacher with the specified document is
     *                                  found
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

    /**
     * Deletes a teacher from the system.
     * 
     * @param teacherDto the data transfer object containing the teacher's document
     *                   ID
     * @return a confirmation message upon successful deletion
     * @throws TeacherNotExistException if no teacher with the specified document is
     *                                  found
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

    /**
     * Updates the information of an existing teacher.
     * 
     * @param teacherDto the data transfer object containing updated teacher details
     * @return a confirmation message upon successful update
     * @throws TeacherNotExistException if no teacher with the specified document is
     *                                  found
     */
    @Override
    @RolesAllowed({ "teacher" })
    @Transactional
    public String updateTeacher(TeacherDto teacherDto) {
        logger.info("Attempting to update teacher with Cedula: {}", teacherDto.cedula());

        Optional<Teacher> teacherOptional = teacherRepository.findByCedula(teacherDto.cedula());

        if (teacherOptional.isEmpty()) {
            logger.warn("Teacher with Cedula {} not found", teacherDto.cedula());
            throw new TeacherNotExistException();
        }

        Teacher teacher = teacherOptional.get();
        teacher.setName(teacherDto.name());
        teacher.setEmail(teacherDto.email());
        teacher.setPassword(hashPassword(teacherDto.password()));
        teacherRepository.persist(teacher);

        logger.info("Teacher with Cedula {} updated successfully", teacherDto.cedula());
        return "The teacher has been updated";
    }

    /**
     * Constructs a Teacher entity from the given TeacherDto, applying password
     * hashing.
     * 
     * @param teacherDto the data transfer object containing teacher data
     * @return a new Teacher entity
     */
    private Teacher buildTeacherFromDto(TeacherDto teacherDto) {
        String hashedPassword = hashPassword(teacherDto.password());
        return new Teacher(
                teacherDto.id(),
                teacherDto.cedula(),
                teacherDto.name(),
                teacherDto.email(),
                hashedPassword,
                StatusAcountEnum.PENDING,
                "");
    }

    /**
     * Applies bcrypt hashing to the provided plain text password.
     * 
     * @param password the plain text password
     * @return the hashed password
     */
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    /**
     * Builds a TeacherDto from the given Teacher entity, excluding the password.
     * 
     * @param teacher the Teacher entity
     * @return a TeacherDto with corresponding data
     */
    private TeacherDto buildDtoFromTeacher(Teacher teacher) {
        return new TeacherDto(
                teacher.getId(),
                teacher.getCedula(),
                teacher.getName(),
                teacher.getEmail(),
                "",
                teacher.getStatus());
    }
}
