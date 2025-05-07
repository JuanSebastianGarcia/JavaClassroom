package co.uniquindio.ingesis.dto.ReportResource;

public record ResolvedProgramReportDto(
                Integer studentId,
                String studentName,
                Long resolvedCount,
                Integer teacherId) {
}
