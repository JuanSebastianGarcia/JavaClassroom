package co.uniquindio.ingesis.service.implement;

import co.uniquindio.ingesis.dto.FeedbackResource.FeedbackDto;
import co.uniquindio.ingesis.dto.FeedbackResource.FeedbackResponseDto;
import co.uniquindio.ingesis.model.Feedback;
import co.uniquindio.ingesis.model.Program;
import co.uniquindio.ingesis.model.Teacher;
import co.uniquindio.ingesis.repository.FeedbackRepository;
import co.uniquindio.ingesis.repository.ProgramRepository;
import co.uniquindio.ingesis.repository.TeacherRepository;
import co.uniquindio.ingesis.service.interfaces.FeedbackServiceInterface;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class FeedbackService implements FeedbackServiceInterface {

    @Inject
    FeedbackRepository feedbackRepository;

    @Inject
    TeacherRepository teacherRepository;

    @Inject
    ProgramRepository programRepository;

@Override
@Transactional
public FeedbackResponseDto agregarFeedback(FeedbackDto dto) {

    Program program = programRepository.findByIdOptional(dto.programId())
            .orElseThrow(() -> new IllegalArgumentException("Programa no encontrado con ID: " + dto.programId()));

    Teacher teacher = teacherRepository.findByIdOptional(dto.teacherId().longValue())
            .orElseThrow(() -> new IllegalArgumentException("Profesor no encontrado con ID: " + dto.teacherId()));

    Feedback feedback = new Feedback();
    feedback.setProgram(program);
    feedback.setTeacher(teacher);
    feedback.setComentario(dto.comment());
    feedback.setFecha(LocalDateTime.now());

    feedbackRepository.persist(feedback);

return new FeedbackResponseDto(
        feedback.getId(),
        feedback.getComentario(),
        feedback.getFecha(),
        teacher.getId().longValue(),   // Convertimos Integer a Long
        teacher.getName(),
        teacher.getEmail(),
        program.getId().longValue(),   // Convertimos Integer a Long
        program.getName()
);
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
                        program.getName()
                );
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
            .orElseThrow(() -> new IllegalArgumentException("Feedback no encontrado con ID: " + feedbackId));

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
            program.getName()
    );
}

@Override
@Transactional
public void eliminarFeedback(Long feedbackId) {
    Feedback feedback = feedbackRepository.findByIdOptional(feedbackId)
            .orElseThrow(() -> new IllegalArgumentException("Feedback no encontrado con ID: " + feedbackId));

    feedbackRepository.delete(feedback);
}
}
