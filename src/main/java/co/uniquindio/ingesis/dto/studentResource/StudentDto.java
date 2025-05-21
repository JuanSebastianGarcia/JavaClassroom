package co.uniquindio.ingesis.dto.studentResource;

import co.uniquindio.ingesis.model.enumerations.StatusAcountEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) used for operations related to Student entity,
 * including registration, retrieval, deletion, and updates.
 *
 * @param id       the unique identifier of the student
 * @param cedula   the student's national ID number (max 10 digits)
 * @param name     the full name of the student
 * @param email    the email address of the student
 * @param password the student's password (minimum length of 12 characters)
 * @param status   the current account status of the student
 */
public record StudentDto(

                @Positive int id,

                @NotBlank @Size(max = 10, message = "Maximum length is 10 characters") String cedula,

                @NotBlank String name,

                @NotBlank String email,

                @NotBlank @Size(min = 12, message = "Password must be at least 12 characters long") String password,

                StatusAcountEnum status) {
}
