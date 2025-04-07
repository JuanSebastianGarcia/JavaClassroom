package co.uniquindio.ingesis.service.interfaces;


public interface StudentProgramServiceInterface {
    boolean isResolved(Integer studentId, Integer programId);
    void markProgramResolved(Long studentId, Long programId, Boolean resolved);
    void deleteProgramResolution(Long studentId, Long programId);

}
