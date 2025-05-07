package co.uniquindio.ingesis.service.implement;

import co.uniquindio.ingesis.dto.FeedbackResource.FeedbackDto;
import co.uniquindio.ingesis.dto.FeedbackResource.FeedbackResponseDto;
import co.uniquindio.ingesis.model.Feedback;
import co.uniquindio.ingesis.model.Program;
import co.uniquindio.ingesis.model.Student;
import co.uniquindio.ingesis.model.Teacher;
import co.uniquindio.ingesis.repository.FeedbackRepository;
import co.uniquindio.ingesis.repository.ProgramRepository;
import co.uniquindio.ingesis.repository.StudentRepository;
import co.uniquindio.ingesis.repository.TeacherRepository;
import co.uniquindio.ingesis.service.interfaces.FeedbackServiceInterface;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ApplicationScoped
public class FeedbackService implements FeedbackServiceInterface {

        @Inject
        FeedbackRepository feedbackRepository;

        @Inject
        TeacherRepository teacherRepository;

        @Inject
        ProgramRepository programRepository;

        @Inject
        VerificationService verificationService;

        @Inject
        StudentRepository studentRepository;

        private static final Logger logger = LogManager.getLogger(TeacherService.class);

        @Override
        public FeedbackResponseDto agregarFeedback(FeedbackDto dto) {
                // Operación transaccional principal
                FeedbackData feedbackData = createFeedbackTransactional(dto);

                // Envío de notificación fuera de la transacción
                try {
                        verificationService.sendCommentNotification(
                                        feedbackData.student().getEmail(),
                                        feedbackData.program().getName());
                        logger.info("Notificación enviada al estudiante {}", feedbackData.student().getEmail());
                } catch (Exception e) {
                        logger.error("Error al enviar notificación: {}", e.getMessage(), e);
                }

                return buildResponseDto(feedbackData);
        }

        @Transactional(Transactional.TxType.REQUIRES_NEW)
        public FeedbackData createFeedbackTransactional(FeedbackDto dto) {
                // Validación y obtención de entidades
                Program program = programRepository.findByIdOptional(dto.programId())
                                .orElseThrow(() -> {
                                        logger.error("Programa no encontrado con ID: {}", dto.programId());
                                        return new IllegalArgumentException("Programa no encontrado");
                                });

                Teacher teacher = teacherRepository.findByIdOptional(dto.teacherId().longValue())
                                .orElseThrow(() -> {
                                        logger.error("Profesor no encontrado con ID: {}", dto.teacherId());
                                        return new IllegalArgumentException("Profesor no encontrado");
                                });

                // Validación de estudiante
                Student student = studentRepository.findByIdOptional(program.getStudentId().longValue())
                                .orElseThrow(() -> {
                                        logger.error("Estudiante no encontrado con ID: {}", program.getStudentId());
                                        return new IllegalArgumentException("Estudiante no encontrado");
                                });

                if (!program.getStudentId().equals(student.getId())) {
                        logger.error("Inconsistencia en IDs. Programa: {}, Estudiante: {}",
                                        program.getStudentId(), student.getId());
                        throw new IllegalStateException("Inconsistencia en los datos del estudiante");
                }

                // Creación y persistencia del feedback
                Feedback feedback = new Feedback();
                feedback.setProgram(program);
                feedback.setTeacher(teacher);
                feedback.setComentario(dto.comment());
                feedback.setFecha(LocalDateTime.now());

                feedbackRepository.persist(feedback);
                logger.info("Feedback creado exitosamente para el programa ID: {}", program.getId());

                return new FeedbackData(feedback, teacher, program, student);
        }

        // Record auxiliar para transportar los datos entre métodos
        private record FeedbackData(
                        Feedback feedback,
                        Teacher teacher,
                        Program program,
                        Student student) {
        }

        private FeedbackResponseDto buildResponseDto(FeedbackData data) {
                return new FeedbackResponseDto(
                                data.feedback().getId(),
                                data.feedback().getComentario(),
                                data.feedback().getFecha(),
                                data.teacher().getId().longValue(),
                                data.teacher().getName(),
                                data.teacher().getEmail(),
                                data.program().getId().longValue(),
                                data.program().getName());
        }

        @Override
        public List<FeedbackResponseDto> obtenerFeedbackPorPrograma(Long programId) {
                return feedbackRepository.findByProgramId(programId.intValue())
                                .stream()
                                .map(feedback -> {
                                        var teacher = feedback.getTeacher();
                                        var program = feedback.getProgram();

                                        return new FeedbackResponseDto(
                                                        feedback.getId(),
                                                        feedback.getComentario(),
                                                        feedback.getFecha(),
                                                        teacher.getId().longValue(),
                                                        teacher.getName(),
                                                        teacher.getEmail(),
                                                        program.getId().longValue(),
                                                        program.getName());
                                })
                                .toList();
        }

        public List<Feedback> obtenerFeedbackPorProfesor(Integer teacherId) {
                return feedbackRepository.findByTeacherId(teacherId);
        }

        @Override
        @Transactional
        public FeedbackResponseDto actualizarFeedback(Long feedbackId, FeedbackDto dto) {
                Feedback feedback = feedbackRepository.findByIdOptional(feedbackId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Feedback no encontrado con ID: " + feedbackId));

                feedback.setComentario(dto.comment());
                feedback.setFecha(LocalDateTime.now());

                feedbackRepository.persist(feedback);

                Teacher teacher = feedback.getTeacher();
                Program program = feedback.getProgram();

                return new FeedbackResponseDto(
                                feedback.getId(),
                                feedback.getComentario(),
                                feedback.getFecha(),
                                teacher.getId().longValue(),
                                teacher.getName(),
                                teacher.getEmail(),
                                program.getId().longValue(),
                                program.getName());
        }

        @Override
        @Transactional
        public void eliminarFeedback(Long feedbackId) {
                Feedback feedback = feedbackRepository.findByIdOptional(feedbackId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Feedback no encontrado con ID: " + feedbackId));

                feedbackRepository.delete(feedback);
        }
}
