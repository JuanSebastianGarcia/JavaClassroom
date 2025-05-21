package co.uniquindio.ingesis.dto.FeedbackResource;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing the detailed response for feedback.
 *
 * @param id           the unique identifier of the feedback
 * @param comentario   the feedback comment text
 * @param fecha        the date and time when the feedback was created
 * @param teacherId    the ID of the teacher who provided the feedback
 * @param teacherName  the full name of the teacher
 * @param teacherEmail the email address of the teacher
 * @param programId    the ID of the program associated with the feedback
 * @param programName  the name of the program associated with the feedback
 */
public record FeedbackResponseDto(
        Long id,
        String comentario,
        LocalDateTime fecha,
        Long teacherId,
        String teacherName,
        String teacherEmail,
        Long programId,
        String programName) {
}
