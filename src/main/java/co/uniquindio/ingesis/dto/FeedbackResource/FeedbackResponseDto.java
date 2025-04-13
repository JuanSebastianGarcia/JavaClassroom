package co.uniquindio.ingesis.dto.FeedbackResource;


import java.time.LocalDateTime;

public record FeedbackResponseDto(
    Long id,
    String comentario,
    LocalDateTime fecha,
    Long teacherId,
    String teacherName,
    String teacherEmail,
    Long programId,
    String programName
) {}