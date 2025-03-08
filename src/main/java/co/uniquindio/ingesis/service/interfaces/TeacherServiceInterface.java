package co.uniquindio.ingesis.service.interfaces;

import co.uniquindio.ingesis.dto.teacherResource.TeacherDto;
import co.uniquindio.ingesis.exception.TeacherExistException;
import co.uniquindio.ingesis.exception.TeacherNotExistException;
import jakarta.transaction.Transactional;

public interface TeacherServiceInterface {

    @Transactional
    String addTeacher(TeacherDto teacherDto) throws TeacherExistException;

    TeacherDto getTeacher(TeacherDto teacherDto) throws TeacherNotExistException;

    String deleteTeacher(TeacherDto teacherDto) throws TeacherNotExistException;

    String updateTeacher(TeacherDto teacherDto) throws TeacherNotExistException;
}