package co.uniquindio.ingesis.service.interf;

import co.uniquindio.ingesis.dto.studentResource.StudentDto;

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
    String deleteStuddent(StudentDto studentDto);



    /*
     * Method to update a studet
     */
    String updadateStudent(StudentDto studentDto);


}
