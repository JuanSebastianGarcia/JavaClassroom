package co.uniquindio.ingesis.dto.programResource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProgramDto(
                Integer id, // Program ID
                @NotBlank String code, // Program code (unique)
                @NotBlank String name, // Program name
                @NotBlank String description, // Program description
                @NotNull Integer studentId, // ID del estudiante
                boolean shared // Nuevo campo
) {
}