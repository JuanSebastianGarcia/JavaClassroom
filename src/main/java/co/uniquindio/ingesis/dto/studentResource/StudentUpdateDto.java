package co.uniquindio.ingesis.dto.studentResource;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object (DTO) for updating student information,
 * including name, email, and password change.
 *
 * @param nombre       the student's full name
 * @param email        the student's email address
 * @param password     the student's current password (required)
 * @param new_password the student's new password (optional)
 */
public record StudentUpdateDto(

        String nombre,

        String email,

        @NotBlank String password,

        String new_password

) {
}
