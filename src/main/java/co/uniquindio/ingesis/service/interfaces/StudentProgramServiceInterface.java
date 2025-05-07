package co.uniquindio.ingesis.service.interfaces;

import java.util.List;

import co.uniquindio.ingesis.dto.ReportResource.ResolvedProgramReportDto;
import co.uniquindio.ingesis.model.Program;

public interface StudentProgramServiceInterface {
    boolean isResolved(Integer studentId, Integer programId);

    void markProgramResolved(Long studentId, Long programId, Boolean resolved, Long teacherId);

    void deleteProgramResolution(Long studentId, Long programId);

    List<ResolvedProgramReportDto> getResolvedProgramReport();

    List<Program> getAllPrograms();

}
