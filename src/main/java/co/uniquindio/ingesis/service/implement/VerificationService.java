package co.uniquindio.ingesis.service.implement;

import java.util.Optional;
import java.util.UUID;

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
    @Channel("canal-mensajes-out")  // Este nombre lo usarás en application.properties
    Emitter<String> mensajeEmitter;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String sendVerificationEmail(String email, String tocken) {
        String verificationCode = UUID.randomUUID().toString();

        MensajeDTO mensaje = new MensajeDTO(
            "EMAIL",
            email,
            "Tu código de verificación es: " + verificationCode,
            "Verifica tu cuenta"
        );

        try {
            String json = objectMapper.writeValueAsString(mensaje);
            System.out.println("Enviando mensaje: " + json);
            mensajeEmitter.send(json);
            System.out.println("Mensaje enviado " + json);
            
        } catch (Exception e) {
            e.printStackTrace(); // Puedes lanzar una excepción si quieres manejarlo mejor
        }

        return verificationCode;
    }

    @Override
    @Transactional
    public void verifyAccount(String email, String verificationCode) {
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent() && student.get().getToken().equals(verificationCode)) {
            student.get().setStatus(StatusAcountEnum.ACTIVE);
            studentRepository.persist(student.get());
            return;
        }

        Optional<Teacher> teacher = teacherRepository.findByEmail(email);
        if (teacher.isPresent() && teacher.get().getToken().equals(verificationCode)) {
            teacher.get().setStatus(StatusAcountEnum.ACTIVE);
            teacherRepository.persist(teacher.get());
            return;
        }

        throw new IllegalArgumentException("Invalid verification code");
    }
}
