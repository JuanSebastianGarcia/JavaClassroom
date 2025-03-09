package co.uniquindio.ingesis.service.implement;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

import co.uniquindio.ingesis.dto.login.LoginDto;
import co.uniquindio.ingesis.dto.login.TokenResponseDto;
import co.uniquindio.ingesis.exception.PasswordIncorrectException;
import co.uniquindio.ingesis.exception.RoleUnknownException;
import co.uniquindio.ingesis.exception.StudentNotExistException;
import co.uniquindio.ingesis.model.Student;
import co.uniquindio.ingesis.model.Teacher;
import co.uniquindio.ingesis.repository.StudentRepository;
import co.uniquindio.ingesis.repository.TeacherRepository;
import co.uniquindio.ingesis.service.interfaces.AuthServiceInterface;
import io.smallrye.jwt.build.Jwt;

/**
 * AuthService is responsible for handling authentication logic, including login validation
 * and JWT token generation for students and teachers.
 */
public class AuthService implements AuthServiceInterface {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    /**
     * Constructor for AuthService.
     * 
     * @param studentRepository Repository to handle student data access
     * @param teacherRepository Repository to handle teacher data access
     */
    public AuthService(StudentRepository studentRepository, TeacherRepository teacherRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    /**
     * Authenticates a user based on role and generates a JWT token.
     * 
     * @param loginDto The login credentials and role.
     * @return TokenResponseDto containing the JWT token.
     * @throws RoleUnknownException       If the role provided is unknown.
     * @throws PasswordIncorrectException If the password is incorrect.
     */
    @Override
    public TokenResponseDto loginUser(LoginDto loginDto) throws RoleUnknownException, PasswordIncorrectException {
        String role = loginDto.role();
        String token;

        switch (role) {
            case "student":
                token = generateTokenForStudent(loginDto);
                break;
            case "teacher":
                token = generateTokenForTeacher(loginDto);
                break;
            default:
                throw new RoleUnknownException();
        }

        return new TokenResponseDto(token);
    }

    /**
     * Authenticates a student and generates a JWT token if credentials are correct.
     * 
     * @param loginDto The login credentials.
     * @return A JWT token for the student.
     * @throws PasswordIncorrectException If the password is incorrect.
     */
    private String generateTokenForStudent(LoginDto loginDto) throws PasswordIncorrectException {
        Optional<Student> student = studentRepository.findByEmail(loginDto.email());

        // Use orElseThrow to simplify optional handling
        Student foundStudent = student.orElseThrow(StudentNotExistException::new);

        validatePassword(loginDto.password(), foundStudent.getPassword());

        return generateToken(foundStudent.getEmail(), "student");
    }

    /**
     * Authenticates a teacher and generates a JWT token if credentials are correct.
     * 
     * @param loginDto The login credentials.
     * @return A JWT token for the teacher.
     * @throws PasswordIncorrectException If the password is incorrect.
     */
    private String generateTokenForTeacher(LoginDto loginDto) throws PasswordIncorrectException {
        Optional<Teacher> teacher = teacherRepository.findByEmail(loginDto.email());

        // Use orElseThrow to simplify optional handling
        Teacher foundTeacher = teacher.orElseThrow(StudentNotExistException::new);

        validatePassword(loginDto.password(), foundTeacher.getPassword());

        return generateToken(foundTeacher.getEmail(), "teacher");
    }

    /**
     * Generates a JWT token with user information.
     * 
     * @param email The user's email.
     * @param role  The user's role.
     * @return A signed JWT token.
     */
    public static String generateToken(String email, String role) {
        Instant now = Instant.now();

        return Jwt.issuer("javaclassroom")
                .subject(email)
                .upn(email)
                .groups(role)
                .expiresAt(now.plus(Duration.ofDays(30)))
                .issuedAt(now)
                .sign();
    }

    /**
     * Validates a hashed password using BCrypt.
     * 
     * @param rawPassword     The plain text password.
     * @param hashedPassword  The hashed password stored in the database.
     * @throws PasswordIncorrectException If the password does not match.
     */
    private void validatePassword(String rawPassword, String hashedPassword) throws PasswordIncorrectException {
        if (!BCrypt.checkpw(rawPassword, hashedPassword)) {
            throw new PasswordIncorrectException();
        }
    }
}
