package co.uniquindio.ingesis.service.interfaces;

import co.uniquindio.ingesis.dto.teacherResource.TeacherDto;
import co.uniquindio.ingesis.exception.TeacherExistException;
import co.uniquindio.ingesis.exception.TeacherNotExistException;
import jakarta.transaction.Transactional;

/**
 * Interface defining operations for managing teachers.
 */
public interface TeacherServiceInterface {

    /**
     * Adds a new teacher.
     *
     * @param teacherDto the data transfer object containing teacher information
     * @return a confirmation message
     * @throws TeacherExistException if a teacher with the same identifier already
     *                               exists
     */
    @Transactional
    String addTeacher(TeacherDto teacherDto) throws TeacherExistException;

    /**
     * Retrieves a teacher by their details.
     *
     * @param teacherDto the data transfer object containing teacher identification
     *                   details
     * @return the full teacher data transfer object
     * @throws TeacherNotExistException if the teacher does not exist
     */
    TeacherDto getTeacher(TeacherDto teacherDto) throws TeacherNotExistException;

    /**
     * Deletes a teacher.
     *
     * @param teacherDto the data transfer object containing teacher identification
     *                   details
     * @return a confirmation message
     * @throws TeacherNotExistException if the teacher does not exist
     */
    String deleteTeacher(TeacherDto teacherDto) throws TeacherNotExistException;

    /**
     * Updates teacher information.
     *
     * @param teacherDto the data transfer object containing updated teacher
     *                   information
     * @return a confirmation message
     * @throws TeacherNotExistException if the teacher does not exist
     */
    String updateTeacher(TeacherDto teacherDto) throws TeacherNotExistException;
}
