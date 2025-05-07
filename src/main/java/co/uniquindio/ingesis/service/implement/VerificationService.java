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

    @Override
    public String sendVerificationEmail(String email, String token) {

        MensajeDTO mensaje = new MensajeDTO(
                "EMAIL",
                email,
                "Tu código de verificación es: " + token,
                "Verifica tu cuenta");

        try {
            String json = objectMapper.writeValueAsString(mensaje);
            System.out.println("Enviando mensaje: " + json);
            mensajeEmitter.send(json);
            System.out.println("Mensaje enviado " + json);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return token;
    }

    @Override
    public void sendCommentNotification(String email, String programName) {

        MensajeDTO mensaje = new MensajeDTO(
                "EMAIL",
                email,
                "Se ha añadido un nuevo comentario a tu programa: " + programName,
                "Nuevo comentario en tu programa");

        try {
            String json = objectMapper.writeValueAsString(mensaje);
            System.out.println("Enviando notificación de comentario: " + json);
            mensajeEmitter.send(json);
            System.out.println("Notificación enviada " + json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public void verifyAccount(String email, String verificationCode) {
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            System.out.println("Código de verificación para estudiante: " + verificationCode); // Imprimir el código
            System.out.println("Token almacenado para estudiante: " + student.get().getToken()); // Imprimir token
                                                                                                 // almacenado
            if (student.get().getToken().equals(verificationCode)) {
                student.get().setStatus(StatusAcountEnum.ACTIVE);
                studentRepository.persist(student.get());
                return;
            }
        }

        Optional<Teacher> teacher = teacherRepository.findByEmail(email);
        if (teacher.isPresent()) {
            System.out.println("Código de verificación para profesor: " + verificationCode); // Imprimir el código
            System.out.println("Token almacenado para profesor: " + teacher.get().getToken()); // Imprimir token
                                                                                               // almacenado
            if (teacher.get().getToken().equals(verificationCode)) {
                teacher.get().setStatus(StatusAcountEnum.ACTIVE);
                teacherRepository.persist(teacher.get());
                return;
            }
        }

        throw new IllegalArgumentException("Invalid verification code");
    }

}
