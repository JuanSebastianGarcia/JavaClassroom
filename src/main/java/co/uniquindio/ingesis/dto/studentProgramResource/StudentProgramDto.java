package co.uniquindio.ingesis.dto.studentProgramResource;


public record StudentProgramDto(
    Long studentId,
    Long programId,
    boolean resolved
) {}