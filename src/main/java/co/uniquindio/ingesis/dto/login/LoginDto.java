package co.uniquindio.ingesis.dto.login;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO (Data Transfer Object) for user authentication.
 * 
 * This record stores login details, ensuring that all fields
 * are required and cannot be empty.
 *
 * @param email    The user's email address (required).
 * @param password The user's password (required).
 * @param role     The user's role in the system (required).
 */
public record LoginDto(

        @NotBlank // Ensures the email is not empty or just spaces
        @NotNull // Ensures the email is not null
        String email,

        @NotBlank // Ensures the password is not empty or just spaces
        @NotNull // Ensures the password is not null
        String password,

        @NotBlank // Ensures the role is not empty or just spaces
        @NotNull // Ensures the role is not null
        String role

) {
}
