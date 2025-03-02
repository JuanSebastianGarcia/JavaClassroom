package co.uniquindio.ingesis.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record TeacherConsult(
    @NotBlank
    @Email
    String email
    
) {}

