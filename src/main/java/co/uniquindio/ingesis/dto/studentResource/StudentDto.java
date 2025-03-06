package co.uniquindio.ingesis.dto.studentResource;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
 * DTO used to register a new student, search a student, delete a student and update a student
 */
public record StudentDto(
        
        
        /*
         * student id
         */
        int id,

        /*
         * student cedula
         */
        String cedula,


        /*
         * student name
         */
        String name,


        /*
         * student email
         */
        String email,


        /*
         * student password
         */
        @Size()
        String password

        ) {


}
