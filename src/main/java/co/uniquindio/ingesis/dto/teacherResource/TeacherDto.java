package co.uniquindio.ingesis.dto.teacherResource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TeacherDto (   
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

)  {

}
