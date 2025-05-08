package co.uniquindio.ingesis.service.implement;

import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.mindrot.jbcrypt.BCrypt;

import co.uniquindio.ingesis.dto.login.LoginDto;
import co.uniquindio.ingesis.dto.login.TokenResponseDto;
import co.uniquindio.ingesis.exception.AccountNotVerifiedException;
import co.uniquindio.ingesis.exception.PasswordIncorrectException;
import co.uniquindio.ingesis.exception.RoleMismatchException;
import co.uniquindio.ingesis.exception.RoleUnknownException;
import co.uniquindio.ingesis.exception.StudentNotExistException;
import co.uniquindio.ingesis.exception.TeacherNotExistException;
import co.uniquindio.ingesis.model.Student;
import co.uniquindio.ingesis.model.Teacher;
import co.uniquindio.ingesis.model.enumerations.StatusAcountEnum;
import co.uniquindio.ingesis.repository.StudentRepository;
import co.uniquindio.ingesis.repository.TeacherRepository;
import co.uniquindio.ingesis.service.interfaces.AuthServiceInterface;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.util.Base64;

/**
 * AuthService is responsible for handling authentication logic, including login
 * validation
 * and JWT token generation for students and teachers.
 */
@ApplicationScoped
public class AuthService implements AuthServiceInterface {

    @ConfigProperty(name = "jwt.secret.key")
    private String SECRET_KEY;

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
     * @throws RoleUnknownException        If the role provided is unknown.
     * @throws PasswordIncorrectException  If the password is incorrect.
     * @throws AccountNotVerifiedException
     */
    @Override
    public TokenResponseDto loginUser(LoginDto loginDto)
            throws RoleUnknownException, PasswordIncorrectException,
            AccountNotVerifiedException, RoleMismatchException {

        String role = loginDto.role();
        String token;

        switch (role.toLowerCase()) {
            case "student":
                // Validar si el correo corresponde a un teacher
                if (teacherRepository.findByEmail(loginDto.email()).isPresent()) {
                    throw new RoleMismatchException();
                }
                token = generateTokenForStudent(loginDto);
                break;

            case "teacher":
                // Validar si el correo corresponde a un student
                if (studentRepository.findByEmail(loginDto.email()).isPresent()) {
                    throw new RoleMismatchException();
                }
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
     * @throws PasswordIncorrectException  If the password is incorrect.
     * @throws AccountNotVerifiedException
     */
    private String generateTokenForStudent(LoginDto loginDto)
            throws PasswordIncorrectException, AccountNotVerifiedException {
        Optional<Student> student = studentRepository.findByEmail(loginDto.email());

        Student foundStudent = student.orElseThrow(StudentNotExistException::new);

        // Validar que el rol realmente sea student
        if (!"student".equalsIgnoreCase(loginDto.role())) {
            throw new RoleMismatchException();
        }

        if (foundStudent.getStatus().equals(StatusAcountEnum.PENDING)) {
            throw new AccountNotVerifiedException();
        }

        validatePassword(loginDto.password(), foundStudent.getPassword());

        System.out.println("Generating token with cedula: " + foundStudent.getDocument());
        return generateToken(foundStudent.getEmail(), "student", foundStudent.getDocument(), foundStudent.getId());
    }

    /**
     * Authenticates a teacher and generates a JWT token if credentials are correct.
     * 
     * @param loginDto The login credentials.
     * @return A JWT token for the teacher.
     * @throws PasswordIncorrectException  If the password is incorrect.
     * @throws AccountNotVerifiedException
     */
    private String generateTokenForTeacher(LoginDto loginDto)
            throws PasswordIncorrectException, AccountNotVerifiedException {
        Optional<Teacher> teacher = teacherRepository.findByEmail(loginDto.email());

        Teacher foundTeacher = teacher.orElseThrow(TeacherNotExistException::new);

        // Validar que el rol realmente sea teacher
        if (!"teacher".equalsIgnoreCase(loginDto.role())) {
            throw new RoleMismatchException();
        }

        if (foundTeacher.getStatus().equals(StatusAcountEnum.PENDING)) {
            throw new AccountNotVerifiedException();
        }

        validatePassword(loginDto.password(), foundTeacher.getPassword());

        System.out.println("Generating token with cedula: " + foundTeacher.getCedula());
        return generateToken(foundTeacher.getEmail(), "teacher", foundTeacher.getCedula(), foundTeacher.getId());
    }

    /**
     * Generates a JWT token with user information.
     * 
     * @param email The user's email.
     * @param role  The user's role.
     * @return A signed JWT token.
     */
    public String generateToken(String email, String role, String cedula, Integer id) {

        System.out.println("Generando token con:");
        System.out.println("Email: " + email);
        System.out.println("Role: " + role);
        System.out.println("Cédula: " + cedula);
        System.out.println("Id: " + id);

        return Jwts.builder()
                .setSubject(email)
                .setIssuer("classroom")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)))
                .signWith(getSigningKey())
                .claim("role", role)
                .claim("cedula", cedula)
                .claim("id", id)
                .compact();

    }

    /**
     * Validates a hashed password using BCrypt.
     * 
     * @param rawPassword    The plain text password.
     * @param hashedPassword The hashed password stored in the database.
     * @throws PasswordIncorrectException If the password does not match.
     */
    private void validatePassword(String rawPassword, String hashedPassword) throws PasswordIncorrectException {
        if (!BCrypt.checkpw(rawPassword, hashedPassword)) {
            throw new PasswordIncorrectException();
        }
    }

    /**
     * Converts the Base64-encoded string key into a SecretKey for JWT signing.
     *
     * @return A SecretKey object used for signing JWT tokens.
     */
    private SecretKey getSigningKey() {
        // Decode the Base64-encoded secret key
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);

        // Generate a SecretKey using HMAC SHA-256 algorithm
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

}
