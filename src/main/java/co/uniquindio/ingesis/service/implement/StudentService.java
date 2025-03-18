package co.uniquindio.ingesis.service.implement;

import java.util.List;
import java.util.Optional;

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
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

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

    @Inject
    private final VerificationService verificationService;

    /**
     * Constructor for dependency injection.
     *
     * @param studentRepository Repository for accessing student data.
     */
    public StudentService(StudentRepository studentRepository, VerificationService verificationService) {
        this.studentRepository = studentRepository;
        this.verificationService = verificationService;
    
    }



    /**
     * Adds a new student to the system, ensuring no duplicates exist.
     *
     * @param studentDto DTO containing student information.
     * @return Confirmation message upon successful creation.
     * @throws StudentExistException If a student with the same document already exists.
     */
    @Override
    @RolesAllowed({"student"}) 
    @Transactional
    public String addStudent(StudentDto studentDto) throws StudentExistException {
        Student newStudent = buildStudentFromDto(studentDto);

        // Check if the student already exists
        Optional<Student> existingStudent = studentRepository.findByCedula(newStudent.getDocument());
        if (existingStudent.isPresent()) {
            throw new StudentExistException();
        }

        // Send verification email
        newStudent.setToken(verificationService.sendVerificationEmail(newStudent.getEmail()));

        // Persist new student
        studentRepository.persist(newStudent);

        return "The student has been created";
    }


    
    /**
     * Retrieves a student's information by email.
     *
     * @param email The email of the student to retrieve.
     * @return DTO containing student details.
     * @throws StudentNotExistException If the student does not exist.
     */
    @Override
    @RolesAllowed({"student"})    
    @Transactional
    public StudentDto getStudent(String email) {
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
        
        return studentRepository.findAll()
        .page(getAllDto.page(), 10) 
        .stream()
        .map(this::buildDtoFromStudent)
        .toList();
    }


    /**
     * Deletes a student from the system after verifying credentials.
     *
     * @param email The email of the student to delete.
     * @param studentDto DTO containing authentication details.
     * @return Confirmation message upon successful deletion.
     * @throws PasswordIncorrectException If the provided password is incorrect.
     * @throws StudentNotExistException If the student does not exist.
     */
    @Transactional
    @Override
    @RolesAllowed({"student"}) 
    public String deleteStuddent(String email, StudentDto studentDto) throws PasswordIncorrectException, StudentNotExistException {
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
     * @param id The ID of the student to update.
     * @param studentUpdateDto DTO containing updated student information.
     * @return Confirmation message upon successful update.
     * @throws PasswordIncorrectException If the provided password is incorrect.
     */
    @Override
    @RolesAllowed({"student"}) 
    @Transactional
    public String updadateStudent(int id, StudentUpdateDto studentUpdateDto) throws PasswordIncorrectException {
        Student student = studentRepository.findById((long) id);
        if (student == null) {
            throw new StudentNotExistException();
        }
        if (!BCrypt.checkpw(studentUpdateDto.password(), student.getPassword())) {
            throw new PasswordIncorrectException();
        }

        // Update password if provided
        if (studentUpdateDto.new_password() != null && !studentUpdateDto.new_password().isEmpty()) {
            student.setPassword(hashPassword(studentUpdateDto.new_password()));
        }

        // Update email if provided
        if (studentUpdateDto.email() != null && !studentUpdateDto.email().isEmpty()) {
            student.setEmail(studentUpdateDto.email());
        }

        // Update name if provided
        if (studentUpdateDto.nombre() != null && !studentUpdateDto.nombre().isEmpty()) {
            student.setName(studentUpdateDto.nombre());
        }

        studentRepository.persist(student);
        return "The student has been updated";
    }

    /**
     * Converts a StudentDto into a Student entity.
     *
     * @param studentDto The DTO containing student information.
     * @return A Student entity.
     */
    private Student buildStudentFromDto(StudentDto studentDto) {
        return new Student(studentDto.id(), studentDto.cedula(), studentDto.name(), studentDto.email(), hashPassword(studentDto.password()),StatusAcountEnum.PENDING,"");
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
        return new StudentDto(student.getId(), student.getDocument(), student.getName(), student.getEmail(), "",student.getStatus());
    }




}