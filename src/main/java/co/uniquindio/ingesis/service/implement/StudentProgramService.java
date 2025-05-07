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

    @Override
    @Transactional
    public void markProgramResolved(Long studentId, Long programId, Boolean resolved, Long teacherId) {
        Student student = studentRepository.findById(studentId);
        Program program = programRepository.findById(programId);
        Teacher teacher = teacherRepository.findById(teacherId);

        if (student == null || program == null || teacher == null) {
            throw new IllegalArgumentException("Student, Program or Teacher not found");
        }

        StudentProgram sp = studentProgramRepository.findByStudentAndProgram(student.getId(), program.getId())
                .orElse(new StudentProgram(null, student, program, null, false));

        sp.setResolved(resolved);
        sp.setResolvedBy(teacher); // Asignar el profesor
        studentProgramRepository.persist(sp);
    }

    @Override
    public boolean isResolved(Integer studentId, Integer programId) {
        return studentProgramRepository.findByStudentAndProgram(studentId, programId)
                .map(StudentProgram::isResolved)
                .orElse(false);
    }

    @Override
    @Transactional
    public void deleteProgramResolution(Long studentId, Long programId) {
        studentProgramRepository.findByStudentAndProgram(studentId.intValue(), programId.intValue())
                .ifPresent(studentProgramRepository::delete);
    }

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

    @Override
    public List<Program> getAllPrograms() {
        return programRepository.listAll();
    }

}