package co.uniquindio.ingesis.dto.studentResource;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
        @Size(max = 10,message = "the max size is 10 numbers")
        @Positive
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
        @Size(min = 12,message = "the password is very small")
        String password


        
        ) {


}
