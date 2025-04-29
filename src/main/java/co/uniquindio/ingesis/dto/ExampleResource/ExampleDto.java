package co.uniquindio.ingesis.dto.ExampleResource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExampleDto(
        Integer id,

        @NotBlank String title,

        @NotBlank String content,

        @NotBlank String category, // Ejemplo: "POO", "Fundamentos", "Manejo de excepciones", etc.

        @NotNull Integer difficulty, // Nivel: 1 (fácil), 2 (medio), 3 (difícil)

        @NotNull String cedulaProfesor // Campo adicional para la cédula del profesor
) {
}
