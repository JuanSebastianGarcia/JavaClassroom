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
        @NotBlank
        String cedula,


        /*
         * student name
         */
        @NotBlank
        String name,


        /*
         * student email
         */
        @NotBlank
        String email,


        /*
         * student password
         */
        @NotBlank
        @Size(min = 12)
        String password

        ) {


}
