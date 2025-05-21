package co.uniquindio.ingesis.dto.studentProgramResource;

/**
 * Data Transfer Object (DTO) representing the association between a student and
 * a program,
 * including the status of whether the program has been resolved by the student.
 *
 * @param studentId the unique identifier of the student
 * @param programId the unique identifier of the program
 * @param resolved  flag indicating if the student has resolved the program
 *                  (true) or not (false)
 */
public record StudentProgramDto(
        Long studentId,
        Long programId,
        boolean resolved) {
}
