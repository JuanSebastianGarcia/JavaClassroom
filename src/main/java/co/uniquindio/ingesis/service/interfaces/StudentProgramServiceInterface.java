package co.uniquindio.ingesis.service.interfaces;

import java.util.List;

import co.uniquindio.ingesis.dto.ReportResource.ResolvedProgramReportDto;
import co.uniquindio.ingesis.model.Program;

/**
 * Interface for managing student program resolution status and reports.
 */
public interface StudentProgramServiceInterface {

    /**
     * Checks if a student has resolved a specific program.
     *
     * @param studentId the ID of the student
     * @param programId the ID of the program
     * @return true if the student has resolved the program, false otherwise
     */
    boolean isResolved(Integer studentId, Integer programId);

    /**
     * Marks a program as resolved or unresolved for a student.
     *
     * @param studentId the ID of the student
     * @param programId the ID of the program
     * @param resolved  true to mark as resolved, false to mark as unresolved
     * @param teacherId the ID of the teacher performing the action
     */
    void markProgramResolved(Long studentId, Long programId, Boolean resolved, Long teacherId);

    /**
     * Deletes the resolution record of a program for a student.
     *
     * @param studentId the ID of the student
     * @param programId the ID of the program
     */
    void deleteProgramResolution(Long studentId, Long programId);

    /**
     * Retrieves a report of all resolved programs.
     *
     * @return a list of DTOs representing resolved program reports
     */
    List<ResolvedProgramReportDto> getResolvedProgramReport();

    /**
     * Retrieves all programs.
     *
     * @return a list of all Program entities
     */
    List<Program> getAllPrograms();

}
