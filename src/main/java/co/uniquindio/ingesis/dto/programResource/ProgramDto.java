package co.uniquindio.ingesis.dto.programResource;


import jakarta.validation.constraints.NotBlank;

public record ProgramDto (
    Integer id,  // Program ID
    @NotBlank
    String code, // Program code (unique)
    @NotBlank
    String name, // Program name
    @NotBlank
    String description // Program description
) {}