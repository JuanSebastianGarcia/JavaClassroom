package co.uniquindio.ingesis.dto.ExampleResource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) representing an example resource.
 *
 * @param id             the unique identifier of the example (can be null for
 *                       new entries)
 * @param title          the title of the example (must not be blank)
 * @param content        the content or body of the example (must not be blank)
 * @param category       the category of the example, e.g., "OOP",
 *                       "Fundamentals", "Exception Handling" (must not be
 *                       blank)
 * @param difficulty     the difficulty level: 1 (easy), 2 (medium), 3 (hard)
 *                       (must not be null)
 * @param cedulaProfesor the identification number of the professor responsible
 *                       for the example (must not be null)
 */
public record ExampleDto(
                Integer id,

                @NotBlank String title,

                @NotBlank String content,

                @NotBlank String category,

                @NotNull Integer difficulty,

                @NotNull String cedulaProfesor) {
}
