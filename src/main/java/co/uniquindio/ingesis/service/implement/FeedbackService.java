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

/**
 * Service implementation for managing Feedback entities.
 * 
 * This class provides transactional operations for creating, retrieving,
 * updating,
 * and deleting feedback related to programs, as well as notifying students upon
 * feedback creation.
 * It integrates with repositories for data access and uses a verification
 * service to send notifications.
 */
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

        /**
         * Creates a new feedback entry based on the provided DTO, persists it,
         * and triggers a notification to the associated student.
         * 
         * @param dto Data transfer object containing feedback details.
         * @return A response DTO containing the persisted feedback information.
         */
        @Override
        public FeedbackResponseDto agregarFeedback(FeedbackDto dto) {
                // Perform feedback creation within a new transaction
                FeedbackData feedbackData = createFeedbackTransactional(dto);

                // Send notification asynchronously after transaction commit
                try {
                        verificationService.sendCommentNotification(
                                        feedbackData.student().getEmail(),
                                        feedbackData.program().getName());
                        logger.info("Notification sent to student {}", feedbackData.student().getEmail());
                } catch (Exception e) {
                        logger.error("Failed to send notification: {}", e.getMessage(), e);
                }

                return buildResponseDto(feedbackData);
        }

        /**
         * Transactional method responsible for validating input data,
         * retrieving related entities, and persisting the new feedback.
         * 
         * @param dto The DTO containing feedback input data.
         * @return A FeedbackData record containing the persisted feedback and related
         *         entities.
         */
        @Transactional(Transactional.TxType.REQUIRES_NEW)
        public FeedbackData createFeedbackTransactional(FeedbackDto dto) {
                Program program = programRepository.findByIdOptional(dto.programId())
                                .orElseThrow(() -> {
                                        logger.error("Program not found with ID: {}", dto.programId());
                                        return new IllegalArgumentException("Program not found");
                                });

                Teacher teacher = teacherRepository.findByIdOptional(dto.teacherId().longValue())
                                .orElseThrow(() -> {
                                        logger.error("Teacher not found with ID: {}", dto.teacherId());
                                        return new IllegalArgumentException("Teacher not found");
                                });

                Student student = studentRepository.findByIdOptional(program.getStudentId().longValue())
                                .orElseThrow(() -> {
                                        logger.error("Student not found with ID: {}", program.getStudentId());
                                        return new IllegalArgumentException("Student not found");
                                });

                if (!program.getStudentId().equals(student.getId())) {
                        logger.error("Mismatch between program student ID ({}) and student ID ({})",
                                        program.getStudentId(), student.getId());
                        throw new IllegalStateException("Inconsistent student data");
                }

                Feedback feedback = new Feedback();
                feedback.setProgram(program);
                feedback.setTeacher(teacher);
                feedback.setComentario(dto.comment());
                feedback.setFecha(LocalDateTime.now());

                feedbackRepository.persist(feedback);
                logger.info("Successfully created feedback for program ID: {}", program.getId());

                return new FeedbackData(feedback, teacher, program, student);
        }

        /**
         * Helper record to encapsulate feedback-related data for internal processing.
         */
        private record FeedbackData(
                        Feedback feedback,
                        Teacher teacher,
                        Program program,
                        Student student) {
        }

        /**
         * Constructs a response DTO from the provided FeedbackData.
         * 
         * @param data The feedback data encapsulating entities.
         * @return A populated FeedbackResponseDto.
         */
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

        /**
         * Retrieves a list of feedbacks associated with the specified program.
         * 
         * @param programId The ID of the program.
         * @return A list of FeedbackResponseDto objects for the program.
         */
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

        /**
         * Retrieves feedbacks associated with a given teacher.
         * 
         * @param teacherId The teacher's ID.
         * @return A list of Feedback entities related to the teacher.
         */
        public List<Feedback> obtenerFeedbackPorProfesor(Integer teacherId) {
                return feedbackRepository.findByTeacherId(teacherId);
        }

        /**
         * Updates an existing feedback entry identified by the feedback ID with new
         * data.
         * 
         * @param feedbackId The ID of the feedback to update.
         * @param dto        The DTO containing updated feedback data.
         * @return A FeedbackResponseDto reflecting the updated feedback.
         * @throws IllegalArgumentException if the feedback is not found.
         */
        @Override
        @Transactional
        public FeedbackResponseDto actualizarFeedback(Long feedbackId, FeedbackDto dto) {
                Feedback feedback = feedbackRepository.findByIdOptional(feedbackId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Feedback not found with ID: " + feedbackId));

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

        /**
         * Deletes a feedback entity by its ID.
         * 
         * @param feedbackId The ID of the feedback to delete.
         * @throws IllegalArgumentException if the feedback is not found.
         */
        @Override
        @Transactional
        public void eliminarFeedback(Long feedbackId) {
                Feedback feedback = feedbackRepository.findByIdOptional(feedbackId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Feedback not found with ID: " + feedbackId));

                feedbackRepository.delete(feedback);
        }
}
