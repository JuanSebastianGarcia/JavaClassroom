package co.uniquindio.ingesis.service.implement;

import java.util.List;

import co.uniquindio.ingesis.dto.ReportResource.ResolvedProgramReportDto;
import co.uniquindio.ingesis.model.Program;
import co.uniquindio.ingesis.model.Student;
import co.uniquindio.ingesis.model.StudentProgram;
import co.uniquindio.ingesis.model.Teacher;
import co.uniquindio.ingesis.repository.ProgramRepository;
import co.uniquindio.ingesis.repository.StudentProgramRepository;
import co.uniquindio.ingesis.repository.StudentRepository;
import co.uniquindio.ingesis.repository.TeacherRepository;
import co.uniquindio.ingesis.service.interfaces.StudentProgramServiceInterface;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Service implementation responsible for managing the relationship between
 * students and programs,
 * including marking programs as resolved, checking resolution status, deleting
 * resolution records,
 * and generating reports related to resolved programs.
 */
@ApplicationScoped
public class StudentProgramService implements StudentProgramServiceInterface {

    @Inject
    StudentProgramRepository studentProgramRepository;

    @Inject
    StudentRepository studentRepository;

    @Inject
    ProgramRepository programRepository;

    @Inject
    TeacherRepository teacherRepository;

    /**
     * Marks a specific program as resolved or unresolved for a given student,
     * recording the teacher
     * who marked it.
     *
     * @param studentId the ID of the student
     * @param programId the ID of the program
     * @param resolved  boolean flag indicating whether the program is resolved or
     *                  not
     * @param teacherId the ID of the teacher who marks the resolution
     * @throws IllegalArgumentException if any of the provided student, program, or
     *                                  teacher IDs are invalid
     */
    @Override
    @Transactional
    public void markProgramResolved(Long studentId, Long programId, Boolean resolved, Long teacherId) {
        Student student = studentRepository.findById(studentId);
        Program program = programRepository.findById(programId);
        Teacher teacher = teacherRepository.findById(teacherId);

        if (student == null || program == null || teacher == null) {
            throw new IllegalArgumentException("Student, Program, or Teacher not found.");
        }

        StudentProgram sp = studentProgramRepository.findByStudentAndProgram(student.getId(), program.getId())
                .orElse(new StudentProgram(null, student, program, null, false));

        sp.setResolved(resolved);
        sp.setResolvedBy(teacher);
        studentProgramRepository.persist(sp);
    }

    /**
     * Checks if a specific program is marked as resolved for a given student.
     *
     * @param studentId the ID of the student
     * @param programId the ID of the program
     * @return true if the program is marked as resolved for the student; false
     *         otherwise
     */
    @Override
    public boolean isResolved(Integer studentId, Integer programId) {
        return studentProgramRepository.findByStudentAndProgram(studentId, programId)
                .map(StudentProgram::isResolved)
                .orElse(false);
    }

    /**
     * Deletes the resolution record of a specific program for a given student,
     * effectively
     * unmarking the program as resolved.
     *
     * @param studentId the ID of the student
     * @param programId the ID of the program
     */
    @Override
    @Transactional
    public void deleteProgramResolution(Long studentId, Long programId) {
        studentProgramRepository.findByStudentAndProgram(studentId.intValue(), programId.intValue())
                .ifPresent(studentProgramRepository::delete);
    }

    /**
     * Retrieves a report containing students and the count of programs they have
     * resolved,
     * including the ID of the teacher associated with each record.
     *
     * @return a list of ResolvedProgramReportDto objects representing the
     *         resolution summary per student
     */
    @Override
    public List<ResolvedProgramReportDto> getResolvedProgramReport() {
        return studentProgramRepository.countResolvedProgramsByStudent()
                .stream()
                .map(obj -> new ResolvedProgramReportDto(
                        (Integer) obj[0], // studentId
                        (String) obj[1], // studentName
                        (Long) obj[2], // resolvedCount
                        (Integer) obj[3] // teacherId
                ))
                .toList();
    }

    /**
     * Retrieves all programs available in the system.
     *
     * @return a list of all Program entities
     */
    @Override
    public List<Program> getAllPrograms() {
        return programRepository.listAll();
    }
}
