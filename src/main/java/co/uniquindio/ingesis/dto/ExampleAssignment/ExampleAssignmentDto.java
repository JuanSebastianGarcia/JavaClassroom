package co.uniquindio.ingesis.dto.ExampleAssignment;

import jakarta.validation.constraints.NotNull;

public record ExampleAssignmentDto(
        @NotNull Integer exampleId,
        @NotNull String cedulaEstudiante) {
}
