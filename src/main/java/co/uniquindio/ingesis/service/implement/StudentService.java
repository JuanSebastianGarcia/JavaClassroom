package co.uniquindio.ingesis.service.implement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import co.uniquindio.ingesis.dto.studentResource.GetAllDto;
import co.uniquindio.ingesis.dto.studentResource.StudentDto;
import co.uniquindio.ingesis.dto.studentResource.StudentUpdateDto;
import co.uniquindio.ingesis.exception.PasswordIncorrectException;
import co.uniquindio.ingesis.exception.StudentExistException;
import co.uniquindio.ingesis.exception.StudentNotExistException;
import co.uniquindio.ingesis.model.Student;
import co.uniquindio.ingesis.model.enumerations.StatusAcountEnum;
import co.uniquindio.ingesis.repository.StudentRepository;
import co.uniquindio.ingesis.service.interfaces.StudentServiceInterface;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Service responsible for handling student management operations.
 */
@ApplicationScoped
public class StudentService implements StudentServiceInterface {

    /**
     * Repository for accessing student data.
     */
    @Inject
    private StudentRepository studentRepository;

    /**
     * Service for handling email verification.
     */
    @Inject
    private VerificationService verificationService;

    /**
     * Logger for tracking service operations.
     */
    private static final Logger logger = LogManager.getLogger(TeacherService.class);

    /**
     * Adds a new student to the system, ensuring no duplicates exist.
     *
     * @param studentDto DTO containing student information.
     * @return Confirmation message upon successful creation.
     * @throws StudentExistException If a student with the same document already
     *                               exists.
     */
    @Override
    @PermitAll
    public String addStudent(StudentDto studentDto) throws StudentExistException {
        // Ejecuta en una nueva transacción
        String token = createStudentTransactional(studentDto);

        // Envío fuera de la transacción principal
        try {
            verificationService.sendVerificationEmail(studentDto.email(), token);
            logger.info("Correo de verificación enviado a {}", studentDto.email());
        } catch (Exception e) {
            logger.error("Error al enviar correo de verificación: {}", e.getMessage(), e);
        }

        return "The student has been created";
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public String createStudentTransactional(StudentDto studentDto) throws StudentExistException {
        Student newStudent = buildStudentFromDto(studentDto);
        logger.info("Attempting to create student with Cedula: {}", newStudent.getDocument());

        Optional<Student> existingStudent = studentRepository.findByCedula(newStudent.getDocument());
        if (existingStudent.isPresent()) {
            logger.error("The document already exists: {}", newStudent.getDocument());
            throw new StudentExistException();
        }

        // Generar y asignar token
        String token = UUID.randomUUID().toString();
        newStudent.setToken(token);

        // Persistir estudiante
        studentRepository.persist(newStudent);
        logger.info("Student created successfully with Cedula: {}", newStudent.getDocument());

        return token;
    }

    /**
     * Retrieves a student's information by email.
     *
     * @param email The email of the student to retrieve.
     * @return DTO containing student details.
     * @throws StudentNotExistException If the student does not exist.
     */
    @Override
    @Transactional
    public StudentDto getStudent(String email) {
        logger.info("Attempting to search a student: {}", email);

        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isEmpty()) {
            throw new StudentNotExistException();
        }
        return buildDtoFromStudent(student.get());
    }

    /**
     * Retrieves all students in the system.
     *
     * @return List of DTOs containing student details.
     */
    @Override
    public List<StudentDto> getAllStudents(GetAllDto getAllDto) {

        logger.info("Attempting to search all students");

        return studentRepository.findAll()
                .page(getAllDto.page(), 10)
                .stream()
                .map(this::buildDtoFromStudent)
                .toList();
    }

    /**
     * Deletes a student from the system after verifying credentials.
     *
     * @param email      The email of the student to delete.
     * @param studentDto DTO containing authentication details.
     * @return Confirmation message upon successful deletion.
     * @throws PasswordIncorrectException If the provided password is incorrect.
     * @throws StudentNotExistException   If the student does not exist.
     */
    @Transactional
    @Override
    public String deleteStuddent(String email, StudentDto studentDto)
            throws PasswordIncorrectException, StudentNotExistException {

        logger.info("Attempting to delete student with Cedula: {}", studentDto.cedula());

        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isEmpty()) {
            throw new StudentNotExistException();
        }
        if (!BCrypt.checkpw(studentDto.password(), student.get().getPassword())) {
            throw new PasswordIncorrectException();
        }
        studentRepository.delete(student.get());
        return "The student has been deleted";
    }

    /**
     * Updates an existing student's details.
     *
     * @param id               The ID of the student to update.
     * @param studentUpdateDto DTO containing updated student information.
     * @return Confirmation message upon successful update.
     * @throws PasswordIncorrectException If the provided password is incorrect.
     */
    @Override
    @Transactional
    public String updateStudent(StudentUpdateDto studentUpdateDto) throws PasswordIncorrectException {
        logger.info("Attempting to update student with Email: {}", studentUpdateDto.email());

        Optional<Student> student_optional = studentRepository.findByEmail(studentUpdateDto.email());

        if (student_optional.isEmpty()) {
            logger.warn("Student with Emal {} not found", studentUpdateDto.email());
            throw new StudentNotExistException();
        }

        Student student = student_optional.get();

        if (!BCrypt.checkpw(studentUpdateDto.password(), student.getPassword())) {
            throw new PasswordIncorrectException();
        }

        if (studentUpdateDto.new_password() != null && !studentUpdateDto.new_password().isEmpty()) {
            student.setPassword(hashPassword(studentUpdateDto.new_password()));
        }

        if (studentUpdateDto.email() != null && !studentUpdateDto.email().isEmpty()) {
            student.setEmail(studentUpdateDto.email());
        }

        if (studentUpdateDto.nombre() != null && !studentUpdateDto.nombre().isEmpty()) {
            student.setName(studentUpdateDto.nombre());
        }

        studentRepository.persist(student);
        logger.info("Student with Cedula {} updated successfully", studentUpdateDto.email());
        return "The student has been updated";
    }

    /**
     * Converts a StudentDto into a Student entity.
     *
     * @param studentDto The DTO containing student information.
     * @return A Student entity.
     */
    private Student buildStudentFromDto(StudentDto studentDto) {
        return new Student(studentDto.id(), studentDto.cedula(), studentDto.name(), studentDto.email(),
                hashPassword(studentDto.password()), StatusAcountEnum.PENDING, "");
    }

    /**
     * Hashes a password using BCrypt.
     *
     * @param password The plaintext password.
     * @return The hashed password.
     */
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    /**
     * Converts a Student entity into a StudentDto.
     *
     * @param student The Student entity.
     * @return A DTO representation of the student.
     */
    private StudentDto buildDtoFromStudent(Student student) {
        return new StudentDto(student.getId(), student.getDocument(), student.getName(), student.getEmail(), "",
                student.getStatus());
    }

}