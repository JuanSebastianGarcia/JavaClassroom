package co.uniquindio.ingesis.dto.studentResource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for pagination when retrieving all students.
 * This DTO encapsulates pagination details.
 *
 * @param page The page number requested for pagination.
 */
public record GetAllDto(
    
    @NotBlank
    @NotNull
    int page) {
}
