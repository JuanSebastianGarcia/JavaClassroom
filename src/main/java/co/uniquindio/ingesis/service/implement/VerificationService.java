package co.uniquindio.ingesis.service.implement;

import java.util.Optional;

import co.uniquindio.ingesis.dto.MensajeDTO;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import co.uniquindio.ingesis.model.Student;
import co.uniquindio.ingesis.model.Teacher;
import co.uniquindio.ingesis.model.enumerations.StatusAcountEnum;
import co.uniquindio.ingesis.repository.StudentRepository;
import co.uniquindio.ingesis.repository.TeacherRepository;
import co.uniquindio.ingesis.service.interfaces.VerificationServiceInterface;

import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class VerificationService implements VerificationServiceInterface {

    @Inject
    StudentRepository studentRepository;

    @Inject
    TeacherRepository teacherRepository;

    @Inject
    @Channel("canal-mensajes-out")
    Emitter<String> mensajeEmitter;

    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sends a verification email with a token to the specified email address.
     * 
     * @param email the email address to send the verification to
     * @param token the verification token to include in the email
     * @return the token sent
     */
    @Override
    public String sendVerificationEmail(String email, String token) {

        MensajeDTO mensaje = new MensajeDTO(
                "EMAIL",
                email,
                "Your verification code is: " + token,
                "Verify your account");

        try {
            String json = objectMapper.writeValueAsString(mensaje);
            System.out.println("Sending message: " + json);
            mensajeEmitter.send(json);
            System.out.println("Message sent " + json);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return token;
    }

    /**
     * Sends a notification email informing the user about a new comment added
     * to their program.
     * 
     * @param email       the email address to notify
     * @param programName the name of the program that received a new comment
     */
    @Override
    public void sendCommentNotification(String email, String programName) {

        MensajeDTO mensaje = new MensajeDTO(
                "EMAIL",
                email,
                "A new comment has been added to your program: " + programName,
                "New comment on your program");

        try {
            String json = objectMapper.writeValueAsString(mensaje);
            System.out.println("Sending comment notification: " + json);
            mensajeEmitter.send(json);
            System.out.println("Notification sent " + json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Verifies a user's account by matching the provided verification code with the
     * token stored for the user (either student or teacher). If the codes match,
     * the user's account status is set to ACTIVE.
     * 
     * @param email            the email address of the user to verify
     * @param verificationCode the verification code provided by the user
     * @throws IllegalArgumentException if the verification code is invalid
     */
    @Override
    @Transactional
    public void verifyAccount(String email, String verificationCode) {
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            System.out.println("Verification code for student: " + verificationCode);
            System.out.println("Stored token for student: " + student.get().getToken());
            if (student.get().getToken().equals(verificationCode)) {
                student.get().setStatus(StatusAcountEnum.ACTIVE);
                studentRepository.persist(student.get());
                return;
            }
        }

        Optional<Teacher> teacher = teacherRepository.findByEmail(email);
        if (teacher.isPresent()) {
            System.out.println("Verification code for teacher: " + verificationCode);
            System.out.println("Stored token for teacher: " + teacher.get().getToken());
            if (teacher.get().getToken().equals(verificationCode)) {
                teacher.get().setStatus(StatusAcountEnum.ACTIVE);
                teacherRepository.persist(teacher.get());
                return;
            }
        }

        throw new IllegalArgumentException("Invalid verification code");
    }

}
