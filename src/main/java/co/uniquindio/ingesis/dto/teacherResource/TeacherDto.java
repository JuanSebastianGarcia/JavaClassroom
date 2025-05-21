package co.uniquindio.ingesis.dto.teacherResource;

import co.uniquindio.ingesis.model.enumerations.StatusAcountEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) representing a Teacher.
 *
 * @param id       the unique identifier of the teacher
 * @param cedula   the national identification number of the teacher
 * @param name     the full name of the teacher
 * @param email    the email address of the teacher
 * @param password the teacher's password (minimum length: 12 characters)
 * @param status   the account status of the teacher
 */
public record TeacherDto(

        Integer id,

        @NotBlank String cedula,

        @NotBlank String name,

        @NotBlank String email,

        @NotBlank @Size(min = 12) String password,

        StatusAcountEnum status

) {
}
