package co.uniquindio.ingesis.service.implement;

import java.util.Optional;
import java.util.UUID;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import co.uniquindio.ingesis.model.Student;
import co.uniquindio.ingesis.model.enumerations.StatusAcountEnum;
import co.uniquindio.ingesis.repository.StudentRepository;
import co.uniquindio.ingesis.service.interfaces.VerificationServiceInterface;

/**
 * Implementation of the verification service for handling user email verification.
 */
@ApplicationScoped
public class VerificationService implements VerificationServiceInterface {

    @Inject
    Mailer mailer;

    private final StudentRepository studentRepository;

    @Inject
    public VerificationService(StudentRepository studentRepository, Mailer mailer) {
        this.studentRepository = studentRepository;
        this.mailer = mailer;
    }

    /**
     * Generates a verification code and sends a verification email to the user.
     * 
     * @param email The recipient's email address.
     * @return The generated verification code.
     */
    @Override
    public String sendVerificationEmail(String email) {
        String verificationCode = UUID.randomUUID().toString();
        String verificationLink = verificationCode;

        mailer.send(
            Mail.withText(email, "Verifica tu cuenta", "Tu codigo de verificacion es: " + verificationLink)
        );

        return verificationCode;
    }

    /**
     * Validates the verification code and activates the user's account.
     * 
     * @param email The user's email address.
     * @param verificationCode The code that was sent to the user.
     */
    @Override
    @Transactional
    public void verifyAccount(String email, String verificationCode) {

        Optional<Student>student = studentRepository.findByEmail(email);

        if(student.isPresent() && student.get().getToken().equals(verificationCode)) {
            student.get().setStatus(StatusAcountEnum.ACTIVE);
            studentRepository.persist(student.get());
        } else {
            throw new IllegalArgumentException("Invalid verification code");
        }
        
    }
}