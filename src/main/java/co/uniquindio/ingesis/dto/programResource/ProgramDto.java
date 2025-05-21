package co.uniquindio.ingesis.dto.programResource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) representing a program.
 *
 * @param id          the unique identifier of the program
 * @param code        the unique program code
 * @param name        the name of the program
 * @param description a brief description of the program
 * @param studentId   the ID of the student who owns or is associated with the
 *                    program
 * @param shared      flag indicating whether the program is shared (true) or
 *                    private (false)
 */
public record ProgramDto(
        Integer id,

        @NotBlank String code,

        @NotBlank String name,

        @NotBlank String description,

        @NotNull Integer studentId,

        boolean shared) {
}
