package co.uniquindio.ingesis.service.interf;

import co.uniquindio.ingesis.dto.teacherResource.TeacherDto;
import co.uniquindio.ingesis.exception.TeacherExistException;
import co.uniquindio.ingesis.exception.TeacherNotExistException;

public interface TeacherServiceInterface {

    String addTeacher(TeacherDto teacherDto) throws TeacherExistException;

    TeacherDto getTeacher(TeacherDto teacherDto) throws TeacherNotExistException;

    String deleteTeacher(TeacherDto teacherDto) throws TeacherNotExistException;

    String updateTeacher(TeacherDto teacherDto) throws TeacherNotExistException;
}