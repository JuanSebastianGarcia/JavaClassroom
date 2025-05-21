package co.uniquindio.ingesis.dto.ReportResource;

/**
 * Data Transfer Object (DTO) representing a report of programs resolved by a
 * student.
 *
 * @param studentId     the unique identifier of the student
 * @param studentName   the full name of the student
 * @param resolvedCount the total number of programs resolved by the student
 * @param teacherId     the unique identifier of the teacher associated with the
 *                      report
 */
public record ResolvedProgramReportDto(
        Integer studentId,
        String studentName,
        Long resolvedCount,
        Integer teacherId) {
}
