package co.uniquindio.ingesis.service.interf;

import co.uniquindio.ingesis.dto.studentResource.StudentDto;
import co.uniquindio.ingesis.dto.studentResource.StudentUpdateDto;
import co.uniquindio.ingesis.exception.PasswordIncorrextException;
import co.uniquindio.ingesis.exception.StudentNotExistException;

/*
 * Interface used to generate the student service
 */
public interface StudentServiceInterface {


    
    /*
     * Method to add a new student
     */
    String addStudent(StudentDto studentDto);



    /*
     * Method to searh a student by email
     */
    StudentDto getStudent(StudentDto studentDto);



    /*
     * Method to delete a student by email
     */
    String deleteStuddent(StudentDto studentDto) throws PasswordIncorrextException, StudentNotExistException;



    /*
     * Method to update a studet
     */
    String updadateStudent(StudentUpdateDto studentUpdateDto) throws PasswordIncorrextException;


}
