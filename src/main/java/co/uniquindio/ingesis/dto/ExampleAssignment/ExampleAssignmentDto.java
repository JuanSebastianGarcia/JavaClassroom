package co.uniquindio.ingesis.dto.ExampleAssignment;

import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) representing an assignment of an example to a
 * student.
 *
 * @param exampleId        the unique identifier of the example (must not be
 *                         null)
 * @param cedulaEstudiante the student's identification number (must not be
 *                         null)
 */
public record ExampleAssignmentDto(
                @NotNull Integer exampleId,
                @NotNull String cedulaEstudiante) {
}
