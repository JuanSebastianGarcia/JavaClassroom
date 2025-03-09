package co.uniquindio.ingesis.service.interfaces;

import co.uniquindio.ingesis.dto.studentResource.StudentDto;
import co.uniquindio.ingesis.dto.studentResource.StudentUpdateDto;
import co.uniquindio.ingesis.exception.PasswordIncorrectException;
import co.uniquindio.ingesis.exception.StudentNotExistException;

/**
 * Interface defining the contract for student service operations.
 */
public interface StudentServiceInterface {

    /**
     * Adds a new student to the system.
     *
     * @param studentDto Data Transfer Object containing student details.
     * @return A confirmation message upon successful registration.
     */
    String addStudent(StudentDto studentDto);

    /**
     * Retrieves student details using their email.
     *
     * @param email The email of the student to be retrieved.
     * @return StudentDto containing the student's details.
     */
    StudentDto getStudent(String email);

    /**
     * Deletes a student from the system using their email.
     *
     * @param email The email of the student to be deleted.
     * @param studentDto DTO containing authentication data for validation.
     * @return A confirmation message upon successful deletion.
     * @throws PasswordIncorrectException If the password provided is incorrect.
     * @throws StudentNotExistException If the student does not exist in the system.
     */
    String deleteStuddent(String email, StudentDto studentDto) throws PasswordIncorrectException, StudentNotExistException;

    /**
     * Updates student details using their ID.
     *
     * @param id The ID of the student to be updated.
     * @param studentUpdateDto DTO containing updated student details.
     * @return A confirmation message upon successful update.
     * @throws PasswordIncorrectException If the password provided is incorrect.
     */
    String updadateStudent(int id, StudentUpdateDto studentUpdateDto) throws PasswordIncorrectException;
}
